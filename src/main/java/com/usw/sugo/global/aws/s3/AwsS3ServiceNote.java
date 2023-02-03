package com.usw.sugo.global.aws.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.note.notefile.NoteFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.usw.sugo.global.aws.s3.BucketDetailPath.NOTE;

@Service
@RequiredArgsConstructor
public class AwsS3ServiceNote {

    @Value("${cloud.aws.s3.preSignedURL}")
    private String preSignedUrl;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final String defaultNotePath = NOTE.getPath();
    private final AmazonS3Client amazonS3Client;

    public List<String> uploadS3ByNote(MultipartFile[] multipartFiles, Long noteId) throws IOException {
        List<String> imagePathList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            String bucketNameByProductPostId = reIssueBucketNameByNoteId(noteId, fileName);
            ObjectMetadata objectMetadata = initObjectMetaData(multipartFile);
            amazonS3Client.putObject(generatePubObjectRequest(bucketNameByProductPostId, multipartFile, objectMetadata));
            imagePathList.add(preSignedUrl + bucketName + "/" + bucketNameByProductPostId);
        }
        return imagePathList;
    }

    public void deleteS3ByNote(NoteFile noteFile) {
        String[] objectUrls = noteFile.getImageLink().split(",");
        for (String objectUrl : objectUrls) {
            objectUrl = filteringUrl(objectUrl);
            String bucketName = objectUrl.substring(
                    objectUrl.indexOf(".com/") + 5,
                    objectUrl.indexOf("/", objectUrl.indexOf(".com/") + 5));
            String fileName = objectUrl.substring(objectUrl.lastIndexOf("/") + 1);
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        }
    }

    private String filteringUrl(String url) {
        return url
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "");
    }

    private String reIssueBucketNameByNoteId(Long noteId, String fileName) {
        return defaultNotePath + "/" + noteId + "/" + fileName;
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
