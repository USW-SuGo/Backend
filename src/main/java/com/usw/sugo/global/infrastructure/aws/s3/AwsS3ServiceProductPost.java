package com.usw.sugo.global.infrastructure.aws.s3;

import static com.usw.sugo.global.exception.ExceptionType.INTERNAL_UPLOAD_EXCEPTION;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.productpost.productpostfile.ProductPostFile;
import com.usw.sugo.global.exception.CustomException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AwsS3ServiceProductPost {

    @Value("${cloud.aws.s3.preSignedURL}")
    private String preSignedUrl;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final String defaultProductPostPath = BucketDetailPath.PRODUCT_POST.getPath();
    private final AmazonS3 amazonS3Client;

    public List<String> uploadS3(MultipartFile[] multipartFiles, Long productPostId) {
        final List<String> imagePathList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            final String filename = multipartFile.getOriginalFilename();
            final String finalUrl = generateURLByProductPostId(productPostId);
            try {
                amazonS3Client.putObject(
                    generatePutObjectRequest(finalUrl + filename, multipartFile,
                        initObjectMetaData(multipartFile)
                    )
                );
                imagePathList.add(amazonS3Client.getUrl(
                    bucketName,
                    finalUrl + filename).toString());
            } catch (IOException e) {
                throw new CustomException(INTERNAL_UPLOAD_EXCEPTION);
            }
        }
        return imagePathList;
    }

    public void deleteS3ProductPostFile(ProductPostFile productPostFile) {
        final String[] objectUrls = productPostFile.getImageLink().split(",");
        deleteObject(objectUrls);
    }

    private void deleteObject(String[] objectUrls) {
        for (String objectUrl : objectUrls) {
            objectUrl = filteringUrl(objectUrl);
            try {
                final URL url = new URL(objectUrl);
                final String bucketName = url.getHost().substring(0, 17);
                final String key = url.getPath().substring(1);
                amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, key));
            } catch (MalformedURLException e) {
                throw new CustomException(INTERNAL_UPLOAD_EXCEPTION);
            }
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
        final long size = multipartFile.getSize();
        final ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(size);
        return objectMetaData;
    }

    private PutObjectRequest generatePutObjectRequest(
        String fileName, MultipartFile multipartFile, ObjectMetadata objectMetadata
    ) throws IOException {
        return new PutObjectRequest(
            bucketName, fileName, multipartFile.getInputStream(), objectMetadata
        ).withCannedAcl(CannedAccessControlList.PublicRead);
    }
}
