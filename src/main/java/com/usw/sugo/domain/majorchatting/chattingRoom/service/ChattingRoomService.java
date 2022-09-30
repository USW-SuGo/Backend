package com.usw.sugo.domain.majorchatting.chattingRoom.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.majorchatting.ChattingRoom;
import com.usw.sugo.domain.majorchatting.ChattingRoomFile;
import com.usw.sugo.domain.majorchatting.ChattingRoomMessage;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChatRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.FileRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.repository.ChattingRoomRepository;
import com.usw.sugo.domain.majorchatting.chattingRoomFile.repository.ChattingRoomFileRepository;
import com.usw.sugo.domain.majorchatting.chattingRoomMessage.repository.ChattingRoomMessageRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChattingRoomService {

    private final AmazonS3Client amazonS3Client;

    private final ChattingRoomRepository roomRepository;
    private final ChattingRoomMessageRepository messageRepository;
    private final ChattingRoomFileRepository fileRepository;
    private final UserRepository userRepository;

    // 채팅방 생성
    public void createChattingRoom(long buyerId, long sellerId) {
        User buyer = userRepository.findById(buyerId).get();
        User seller = userRepository.findById(sellerId).get();

        ChattingRoom chattingRoom = ChattingRoom.builder()
                .roomValue(UUID.randomUUID().toString())
                .sender(buyer)
                .receiver(seller)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.AVAILABLE.getAuthority())
                .build();
    }

    // 메세지 저장
    public void saveMessages(ChatRequest chatRequest) {
        User sender = userRepository.findById(chatRequest.getSenderId()).get();
        User receiver = userRepository.findById(chatRequest.getReceiverId()).get();
        ChattingRoom chattingRoom = roomRepository.findById(chatRequest.getRoomId()).get();

        ChattingRoomMessage chattingRoomMessage = ChattingRoomMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .message(chatRequest.getMessage())
                .chattingRoomId(chattingRoom)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.AVAILABLE.getAuthority())
                .build();

        messageRepository.save(chattingRoomMessage);
    }

    // 파일 저장
    public void saveFiles(FileRequest fileRequest) throws IOException {
        User sender = userRepository.findById(fileRequest.getSenderId()).get();
        User receiver = userRepository.findById(fileRequest.getReceiverId()).get();
        ChattingRoom chattingRoom = roomRepository.findById(fileRequest.getRoomId()).get();

        List<String> imagePathList = new ArrayList<>();

        for (MultipartFile multipartFile : fileRequest.getMultipartFileList()) {
            String originalName = multipartFile.getOriginalFilename(); // 파일 이름
            long size = multipartFile.getSize(); // 파일 크기

            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType(multipartFile.getContentType());
            objectMetaData.setContentLength(size);

            // S3에 업로드
            amazonS3Client.putObject(
                    new PutObjectRequest("diger-usw-sugo-s3/chatting-resource",
                            originalName, multipartFile.getInputStream(), objectMetaData)
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

        ChattingRoomFile chattingRoomFile = ChattingRoomFile.builder()
                .sender(sender)
                .receiver(receiver)
                .imageLink(imageLinkStringBuilder.toString())
                .chattingRoomId(chattingRoom)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Status.AVAILABLE.getAuthority())
                .build();

        fileRepository.save(chattingRoomFile);
    }
}
