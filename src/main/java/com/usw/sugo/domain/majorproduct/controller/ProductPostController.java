package com.usw.sugo.domain.majorproduct.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.majorproduct.ProductPost;
import com.usw.sugo.domain.majorproduct.ProductPostFile;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PostRequest;
import com.usw.sugo.domain.majorproduct.dto.PostResponseDto.MainPageResponse;
import com.usw.sugo.domain.majorproduct.repository.productpost.ProductPostRepository;
import com.usw.sugo.domain.majorproduct.repository.productpostfile.ProductPostFileRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.status.Status;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class ProductPostController {

    private final AmazonS3Client amazonS3Client;
    private final ProductPostRepository productPostRepository;
    private final ProductPostFileRepository productPostFileRepository;
    private final UserRepository userRepository;
    private final JwtResolver jwtResolver;

    @GetMapping("/all")
    public ResponseEntity<List<MainPageResponse>> loadMainPage(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productPostRepository.loadMainPagePostList(pageable));
    }

    @PostMapping("/content")
    public ResponseEntity<Object> postImage(@RequestHeader String authorization,
                                            @RequestBody PostRequest postRequest) {

        User requestUser = userRepository.findById(
                jwtResolver.jwtResolveToUserId(
                        authorization.substring(6))).get();

        ProductPost productPost = ProductPost.builder()
                .user(requestUser)
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .price(postRequest.getPrice())
                .contactPlace(postRequest.getContactPlace())
                .category(postRequest.getCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.AVAILABLE.getAuthority())
                .build();

        productPostRepository.save(productPost);

        long returnValue = productPost.getId();

        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<>(){{put("productPostId", returnValue);}});

    }

    //@ModelAttribute PostRequest postRequest
    @PostMapping("/image")
    public ResponseEntity<Object> postImage(MultipartFile[] multipartFileList, Long postId) throws IOException {

        List<String> imagePathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            String originalName = multipartFile.getOriginalFilename(); // 파일 이름
            long size = multipartFile.getSize(); // 파일 크기

            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType(multipartFile.getContentType());
            objectMetaData.setContentLength(size);

            // S3에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest("diger-usw-sugo-s3/post-resource", originalName, multipartFile.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );

            String imagePath = amazonS3Client.getUrl("diger-usw-sugo-s3/post-resource", originalName).toString(); // 접근가능한 URL 가져오기
            imagePathList.add(imagePath);

        }

        ProductPost targetPost = productPostRepository.findById(postId).get();
        StringBuilder sb = new StringBuilder();

        if (imagePathList.size() == 1) {
            sb.append(imagePathList.get(0));
        } else if (imagePathList.size() > 1) {
            for (int i = 0; i < imagePathList.size(); i++) {
                if (i == imagePathList.size() - 1) {
                    sb.append(imagePathList.get(i));
                } else {
                    sb.append(imagePathList.get(i) + ",");
                }
            }
        }

        ProductPostFile productPostFile = ProductPostFile.builder()
                .productPost(targetPost)
                .imageLink(String.valueOf(sb))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.AVAILABLE.getAuthority())
                .build();

        productPostFileRepository.save(productPostFile);

        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<>(){{put("Success", true);}});
    }
}
