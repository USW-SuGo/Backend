package com.usw.sugo.domain.note.notefile.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.note.entity.Note;
import com.usw.sugo.domain.note.entity.NoteFile;
import com.usw.sugo.domain.note.note.repository.NoteRepository;
import com.usw.sugo.domain.note.notefile.dto.NoteFileRequestDto.SendNoteFileForm;
import com.usw.sugo.domain.note.notefile.repository.NoteFileRepository;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteFileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final NoteRepository noteRepository;
    private final NoteFileRepository noteFileRepository;
    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;

    public void sendFile(SendNoteFileForm sendNoteFileForm, MultipartFile[] multipartFiles) throws IOException {
        List<String> imagePathList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String originalName = multipartFile.getOriginalFilename();
            long size = multipartFile.getSize();
            ObjectMetadata objectMetaData = new ObjectMetadata();

            objectMetaData.setContentType(multipartFile.getContentType());
            objectMetaData.setContentLength(size);
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName + "/note-resources",
                            originalName, multipartFile.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead));

            String imagePath = amazonS3Client.getUrl(bucketName + "/note-resources", originalName).toString();
            imagePathList.add(imagePath);
        }
        StringBuilder uploadedInS3ImageLink = new StringBuilder();

        if (imagePathList.size() == 1) {
            uploadedInS3ImageLink.append(imagePathList.get(0));
        } else if (imagePathList.size() > 1) {
            for (int i = 0; i < imagePathList.size(); i++) {
                if (i == imagePathList.size() - 1) {
                    uploadedInS3ImageLink.append(imagePathList.get(i));
                } else {
                    uploadedInS3ImageLink.append(imagePathList.get(i)).append(",");
                }
            }
        }
        NoteFile noteFile = NoteFile.builder()
                .noteId(noteRepository.findById(sendNoteFileForm.getNoteId())
                        .orElseThrow(() -> new CustomException(ExceptionType.NOTE_NOT_FOUNDED)))
                .sender(userRepository.findById(sendNoteFileForm.getSenderId())
                        .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_EXIST)))
                .receiver(userRepository.findById(sendNoteFileForm.getReceiverId())
                        .orElseThrow(() -> new CustomException(ExceptionType.USER_NOT_EXIST)))
                .imageLink(uploadedInS3ImageLink.toString())
                .build();

        long unreadUserId = -1;

        Optional<Note> targetNote = noteRepository.findById(sendNoteFileForm.getNoteId());
        if (targetNote.get().getCreatingUser().getId() == sendNoteFileForm.getSenderId()) {
            unreadUserId = targetNote.get().getOpponentUser().getId();
        } else if (targetNote.get().getCreatingUser().getId() != sendNoteFileForm.getSenderId()) {
            unreadUserId = targetNote.get().getCreatingUser().getId();
        }

        noteFileRepository.save(noteFile);
        noteRepository.updateRecentContent(unreadUserId, sendNoteFileForm.getNoteId(), "", noteFile.toString());
    }

    public void deleteNoteFile(User requestUser) {
        List<NoteFile> bySender = noteFileRepository.findBySender(requestUser);
        List<NoteFile> byReceiver = noteFileRepository.findByReceiver(requestUser);

        List<String> deletedTargetS3ObjectImages = new ArrayList<>();

        for (NoteFile noteFile : bySender) {
            deletedTargetS3ObjectImages.add(noteFile.getImageLink());
            noteFileRepository.deleteById(noteFile.getId());
        }
        for (NoteFile noteFile : byReceiver) {
            deletedTargetS3ObjectImages.add(noteFile.getImageLink());
            noteFileRepository.deleteById(noteFile.getId());
        }

        for (String target : deletedTargetS3ObjectImages) {
            amazonS3Client.deleteObject(bucketName, target.substring(58));
        }
    }
}
