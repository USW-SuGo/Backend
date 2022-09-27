package com.usw.sugo.domain.majorchatting.chattingRoomFile.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.majorchatting.ChattingRoomFile;
import com.usw.sugo.domain.majorchatting.chattingRoomFile.repository.ChattingRoomFileRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chatting/file")
public class ChattingRoomFileController {

    private final ChattingRoomFileRepository chattingRoomFileRepository;
    private final AmazonS3Client amazonS3Client;

    private final UserRepository userRepository;

    @PostMapping
    public void postFile(@RequestBody Long senderId, Long receiverId, MultipartFile[] multipartFileList) throws IOException {

        List<String> imagePathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            String originalName = multipartFile.getOriginalFilename(); // 파일 이름
            long size = multipartFile.getSize(); // 파일 크기

            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType(multipartFile.getContentType());
            objectMetaData.setContentLength(size);

            // S3에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest("diger-usw-sugo-s3/chatting-resource", originalName, multipartFile.getInputStream(), objectMetaData)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );

            String imagePath = amazonS3Client.getUrl("diger-usw-sugo-s3/chatting-resource", originalName).toString(); // 접근가능한 URL 가져오기
            imagePathList.add(imagePath);
        }

        StringBuilder imageLinkStringBuilder = new StringBuilder();

        // 문자열 처리, DB에 리스트 형식으로 담기 위함이다.
        if (imagePathList.size() == 1) {
            imageLinkStringBuilder.append(imagePathList.get(0));
        } else if (imagePathList.size() > 1) {
            for (int i = 0; i < imagePathList.size(); i++) {
                if (i == imagePathList.size() - 1) {
                    imageLinkStringBuilder.append(imagePathList.get(i));
                } else {
                    imageLinkStringBuilder.append(imagePathList.get(i) + ",");
                }
            }
        }

        User sender = userRepository.findById(senderId).get();
        User receiver = userRepository.findById(receiverId).get();

        ChattingRoomFile chattingRoomFile = ChattingRoomFile.builder()
                .imageLink(String.valueOf(imageLinkStringBuilder))
                .sender(sender)
                .receiver(receiver)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.AVAILABLE.getAuthority())
                .build();

        chattingRoomFileRepository.save(chattingRoomFile);
    }
}
