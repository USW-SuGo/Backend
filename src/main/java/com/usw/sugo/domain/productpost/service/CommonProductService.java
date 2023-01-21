package com.usw.sugo.domain.productpost.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.productpost.dto.PostRequestDto;
import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.productpostfile.ProductPostFile;
import com.usw.sugo.domain.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.productpostfile.repository.ProductPostFileRepository;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonProductService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final UserRepository userRepository;
    private final JwtResolver jwtResolver;
    private final ProductPostRepository productPostRepository;
    private final ProductPostFileRepository productPostFileRepository;
    private final AmazonS3Client amazonS3Client;
    
    // S3 버킷 객체 생성
    @Transactional
    public StringBuilder savePosting(
            String authorization, PostRequestDto.PostingRequest postingRequest, MultipartFile[] multipartFileList) throws IOException {

        User requestUser = userRepository.findById(jwtResolver.jwtResolveToUserId(authorization.substring(7)))
                .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_EXIST));

        ProductPost productPost = ProductPost.builder()
                .user(requestUser)
                .title(postingRequest.getTitle())
                .content(postingRequest.getContent())
                .price(postingRequest.getPrice())
                .contactPlace(postingRequest.getContactPlace())
                .category(postingRequest.getCategory())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(true)
                .build();

        productPostRepository.save(productPost);

        // 게시글 이미지 링크를 담을 리스트
        List<String> imagePathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            String originalName = multipartFile.getOriginalFilename();
            long size = multipartFile.getSize();

            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType(multipartFile.getContentType());
            objectMetaData.setContentLength(size);

            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName + "/post-resource",
                            originalName, multipartFile.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
            String imagePath = amazonS3Client.getUrl(bucketName + "/post-resource", originalName).toString();
            imagePathList.add(imagePath);
        }

        StringBuilder imageLink = new StringBuilder();

        if (imagePathList.size() == 1) {
            imageLink.append(imagePathList.get(0));
        } else if (imagePathList.size() > 1) {
            for (int i = 0; i < imagePathList.size(); i++) {
                if (i == imagePathList.size() - 1) {
                    imageLink.append(imagePathList.get(i));
                } else {
                    imageLink.append(imagePathList.get(i) + ",");
                }
            }
        }

        ProductPostFile productPostFile = ProductPostFile.builder()
                .productPost(productPost)
                .imageLink(String.valueOf(imageLink))
                .createdAt(LocalDateTime.now())
                .build();
        productPostFileRepository.save(productPostFile);

        return imageLink;
    }

    // S3 버킷 객체 삭제
    @Transactional
    public void deleteS3Content(long productPostId) {
        ProductPost deleteTargetProductPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));

        ProductPostFile deleteTargetProductPostFile = productPostFileRepository
                .findByProductPost(deleteTargetProductPost)
                .orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));

        String[] deletedTargetObject = deleteTargetProductPostFile
                .getImageLink()
                .split(",");

        for (String target : deletedTargetObject) {
            amazonS3Client.deleteObject(bucketName, target.substring(58));
        }
    }

    // S3 에 업데이트 --> 기존 S3 버킷 객체 삭제 후 재 업로드
    @Transactional
    public String updateS3Content(PostRequestDto.PutContentRequest putContentRequest, MultipartFile[] multipartFileList) throws IOException {

        ProductPost updateTargetProductPost = productPostRepository.findById(putContentRequest.getProductPostId())
                .orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));

        ProductPostFile deleteTargetProductPostFile = productPostFileRepository
                .findByProductPost(updateTargetProductPost)
                .orElseThrow(() -> new CustomException(ExceptionType.POST_NOT_FOUND));

        String[] deletedTargetObject = deleteTargetProductPostFile
                .getImageLink()
                .split(",");

        for (String target : deletedTargetObject) {
            amazonS3Client.deleteObject(bucketName, target.substring(58));
        }

        // 게시글 이미지 링크를 담을 리스트
        List<String> imagePathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            // 파일 이름
            String originalName = multipartFile.getOriginalFilename();

            // 파일 크기
            long size = multipartFile.getSize();

            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType(multipartFile.getContentType());
            objectMetaData.setContentLength(size);

            // S3에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName + "/post-resource",
                            originalName, multipartFile.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead));

            // S3 링크 DB에 넣을 준비 -> 접근가능한 URL 가져오기
            String imagePath = amazonS3Client.getUrl(bucketName + "/post-resource", originalName).toString();
            imagePathList.add(imagePath);
        }

        StringBuilder uploadedS3BucketURI = new StringBuilder();

        // 문자열 처리, DB에 리스트 형식으로 담기 위함이다.
        if (imagePathList.size() == 1) {
            uploadedS3BucketURI.append(imagePathList.get(0));
        } else if (imagePathList.size() > 1) {
            for (int i = 0; i < imagePathList.size(); i++) {
                if (i == imagePathList.size() - 1) {
                    uploadedS3BucketURI.append(imagePathList.get(i));
                } else {
                    uploadedS3BucketURI.append(imagePathList.get(i)).append(",");
                }
            }
        }
        return uploadedS3BucketURI.toString();
    }
}
