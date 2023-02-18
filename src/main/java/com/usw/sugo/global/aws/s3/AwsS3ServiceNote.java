package com.usw.sugo.global.aws.s3;

import static com.usw.sugo.global.aws.s3.BucketDetailPath.NOTE;
import static com.usw.sugo.global.exception.ExceptionType.INTERNAL_UPLOAD_EXCEPTION;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.global.exception.CustomException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AwsS3ServiceNote {

    @Value("${cloud.aws.s3.preSignedURL}")
    private String preSignedUrl;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final String defaultNotePath = NOTE.getPath();
    private final AmazonS3Client amazonS3Client;

    public List<String> uploadS3ByNote(MultipartFile[] multipartFiles, Long noteId) {
        List<String> imagePathList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            final String filename = multipartFile.getOriginalFilename();
            final String finalUrl = generateURLByProductPostId(noteId);
            amazonS3Client.putObject(
                generatePutObjectRequest(finalUrl + filename, multipartFile,
                    initObjectMetaData(multipartFile)));
            imagePathList.add(amazonS3Client.getUrl(bucketName, finalUrl + filename).toString());
        }
        return imagePathList;
    }

    public void deleteS3ByNoteFile() {
//        String[] objectUrls = noteFile.getImageLink().split(",");
//        for (String objectUrl : objectUrls) {
//            objectUrl = filteringUrl(objectUrl);
//            amazonS3Client.deleteObject(bucketName, objectUrl);
//        }
        return;
    }

    private String filteringUrl(String url) {
        return url
            .replace("[", "")
            .replace("]", "")
            .replace(" ", "");
    }

    private ObjectMetadata initObjectMetaData(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(multipartFile.getContentType());
        objectMetaData.setContentLength(size);
        return objectMetaData;
    }

    private String generateURLByProductPostId(Long noteId) {
        return defaultNotePath + "/" + noteId + "/";
    }

    private PutObjectRequest generatePutObjectRequest(
        String fileName, MultipartFile multipartFile, ObjectMetadata objectMetadata) {
        try {
            return new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(),
                objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        } catch (IOException e) {
            throw new CustomException(INTERNAL_UPLOAD_EXCEPTION);
        }
    }
}
