package com.usw.sugo.global.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.usw.sugo.global.aws.s3.BucketDetailPath.PRODUCT_POST;
import static com.usw.sugo.global.exception.ExceptionType.INTERNAL_UPLOAD_EXCEPTION;

@Service
@RequiredArgsConstructor
public class AwsS3ServiceProductPost {

    @Value("${cloud.aws.s3.preSignedURL}")
    private String preSignedUrl;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final String defaultProductPostPath = PRODUCT_POST.getPath();
    private final AmazonS3Client amazonS3Client;

    public List<String> uploadS3(MultipartFile[] multipartFiles, Long productPostId) {
        List<String> imagePathList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String filename = multipartFile.getOriginalFilename();
            String generatedUrlByProductPostId = generateURLByProductPostId(productPostId);
            try {
                amazonS3Client.putObject(
                        generatePubObjectRequest(
                                generatedUrlByProductPostId + filename,
                                multipartFile,
                                initObjectMetaData(multipartFile)));
                imagePathList.add(amazonS3Client.getUrl(
                        bucketName,
                        generatedUrlByProductPostId + filename).toString());
            } catch (IOException e) {
                throw new CustomException(INTERNAL_UPLOAD_EXCEPTION);
            }
        }
        return imagePathList;
    }

    public void deleteS3ProductPostFile(ProductPostFile productPostFile) {
        String[] objectUrls = productPostFile.getImageLink().split(",");
        for (String objectUrl : objectUrls) {
            objectUrl = filteringUrl(objectUrl);
            amazonS3Client.deleteObject(bucketName, objectUrl);
        }
    }

    private String filteringUrl(String url) {
        return url
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "");
    }

    private String generateURLByProductPostId(Long productPostId) {
        return defaultProductPostPath + "/" + productPostId + "/";
    }

    private ObjectMetadata initObjectMetaData(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(size);
        return objectMetaData;
    }

    private PutObjectRequest generatePubObjectRequest(
            String fileName, MultipartFile multipartFile, ObjectMetadata objectMetadata) throws IOException {
        return new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
    }
}
