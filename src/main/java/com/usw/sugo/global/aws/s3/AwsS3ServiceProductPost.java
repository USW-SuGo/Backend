package com.usw.sugo.global.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.usw.sugo.global.aws.s3.BucketDetailPath.PRODUCT_POST;

@Service
@RequiredArgsConstructor
public class AwsS3ServiceProductPost {

    @Value("${cloud.aws.s3.preSignedURL}")
    private String preSignedUrl;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final String defaultProductPostPath = PRODUCT_POST.getPath();
    private final AmazonS3Client amazonS3Client;

    public List<String> uploadS3ByProductPost(MultipartFile[] multipartFiles, Long productPostId) throws IOException {
        List<String> imagePathList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            String bucketNameByProductPostId = reIssueBucketNameByProductPostId(productPostId, fileName);
            ObjectMetadata objectMetadata = initObjectMetaData(multipartFile);
            amazonS3Client.putObject(generatePubObjectRequest(bucketNameByProductPostId, multipartFile, objectMetadata));
            imagePathList.add(preSignedUrl + bucketName + "/" + bucketNameByProductPostId);
        }
        return imagePathList;
    }

    public void deleteS3ProductPostFile(ProductPostFile productPostFile) {
        String[] objectUrls = productPostFile.getImageLink().split(",");
        for (String objectUrl : objectUrls) {
            objectUrl = filteringUrl(objectUrl);
            String objectKey = objectUrl.substring(objectUrl.indexOf(bucketName + "/") + bucketName.length() + 1);
            amazonS3Client.deleteObject(bucketName, objectKey);
        }
    }

    private String filteringUrl(String url) {
        return url
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "");
    }

    private String reIssueBucketNameByProductPostId(Long productPostId, String fileName) {
        return defaultProductPostPath + "/" + productPostId + "/" + fileName;
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
