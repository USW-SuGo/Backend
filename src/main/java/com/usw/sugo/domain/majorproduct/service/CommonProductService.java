package com.usw.sugo.domain.majorproduct.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.majorproduct.ProductPost;
import com.usw.sugo.domain.majorproduct.ProductPostFile;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PostingRequest;
import com.usw.sugo.domain.majorproduct.dto.PostRequestDto.PutContentRequest;
import com.usw.sugo.domain.majorproduct.repository.productpost.ProductPostRepository;
import com.usw.sugo.domain.majorproduct.repository.productpostfile.ProductPostFileRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
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
            String authorization, PostingRequest postingRequest, MultipartFile[] multipartFileList) throws IOException {

        User requestUser = userRepository.findById(jwtResolver.jwtResolveToUserId(authorization.substring(7)))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        // 게시글 컨텐츠 도메인
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

        // 게시글 컨텐츠 저장
        productPostRepository.save(productPost);

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
                    new PutObjectRequest(bucketName + "/post-resource", originalName, multipartFile.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead));

            // S3 링크 DB에 넣을 준비 -> 접근가능한 URL 가져오기
            String imagePath = amazonS3Client.getUrl(bucketName + "/post-resource", originalName).toString();
            imagePathList.add(imagePath);
        }

        StringBuilder sb = new StringBuilder();

        // 문자열 처리, DB에 리스트 형식으로 담기 위함이다.
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

        // 게시글 이미지 도메인 생성
        ProductPostFile productPostFile = ProductPostFile.builder()
                .productPost(productPost)
                .imageLink(String.valueOf(sb))
                .createdAt(LocalDateTime.now())
                .build();

        // 게시글 이미지 저장
        productPostFileRepository.save(productPostFile);

        return sb;
    }

    // S3 버킷 객체 삭제
    @Transactional
    public void deleteS3Content(long productPostId) {
        ProductPost deleteTargetProductPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        ProductPostFile deleteTargetProductPostFile = productPostFileRepository
                .findByProductPost(deleteTargetProductPost)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        String[] deletedTargetObject = deleteTargetProductPostFile
                .getImageLink()
                .split(",");

        for (String target : deletedTargetObject) {
            amazonS3Client.deleteObject(bucketName, target.substring(58));
        }
    }

    // S3 에 업데이트 --> 기존 S3 버킷 객체 삭제 후 재 업로드
    @Transactional
    public String updateS3Content(PutContentRequest putContentRequest, MultipartFile[] multipartFileList) throws IOException {

        ProductPost updateTargetProductPost = productPostRepository.findById(putContentRequest.getProductPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        ProductPostFile deleteTargetProductPostFile = productPostFileRepository
                .findByProductPost(updateTargetProductPost)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

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
                    new PutObjectRequest(bucketName + "/post-resource", originalName, multipartFile.getInputStream(), objectMetaData)
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
