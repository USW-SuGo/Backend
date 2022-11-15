package com.usw.sugo.domain.note.notefile.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.note.entity.NoteFile;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.note.notefile.dto.NoteFileRequestDto.SendNoteFileForm;
import com.usw.sugo.domain.note.notefile.repository.NoteFileRepository;
import com.usw.sugo.domain.productpost.entity.ProductPostFile;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
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
public class NoteFileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteFileRepository noteFileRepository;
    private final AmazonS3Client amazonS3Client;

    public void sendFile(SendNoteFileForm sendNoteFileForm, MultipartFile[] multipartFiles) throws IOException {

        // 게시글 이미지 링크를 담을 리스트
        List<String> imagePathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            // 파일 이름
            String originalName = multipartFile.getOriginalFilename();

            // 파일 크기
            long size = multipartFile.getSize();

            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType(multipartFile.getContentType());
            objectMetaData.setContentLength(size);

            // S3에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName + "/note-resources", originalName, multipartFile.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead));

            // S3 링크 DB에 넣을 준비 -> 접근가능한 URL 가져오기
            String imagePath = amazonS3Client.getUrl(bucketName + "/note-resources", originalName).toString();
            imagePathList.add(imagePath);
        }

        StringBuilder uploadedInS3ImageLink = new StringBuilder();

        // 문자열 처리, DB에 리스트 형식으로 담기 위함이다.
        if (imagePathList.size() == 1) {
            uploadedInS3ImageLink.append(imagePathList.get(0));
        } else if (imagePathList.size() > 1) {
            for (int i = 0; i < imagePathList.size(); i++) {
                if (i == imagePathList.size() - 1) {
                    uploadedInS3ImageLink.append(imagePathList.get(i));
                } else {
                    uploadedInS3ImageLink.append(imagePathList.get(i) + ",");
                }
            }
        }


        NoteFile noteFile = NoteFile.builder()
                .noteId(noteRepository.findById(sendNoteFileForm.getNoteId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOTE_NOT_FOUNDED)))
                .sender(userRepository.findById(sendNoteFileForm.getSenderId())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST)))
                .receiver(userRepository.findById(sendNoteFileForm.getReceiverId())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST)))
                .imageLink(uploadedInS3ImageLink.toString())
                .build();

        // 게시글 이미지 저장
        noteFileRepository.save(noteFile);
        noteRepository.updateRecentContent(sendNoteFileForm.getNoteId(), "", noteFile.toString());
    }
}
