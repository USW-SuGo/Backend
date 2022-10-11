package com.usw.sugo.domain.majorchatting.chattingRoomMessaging.controller;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.majorchatting.ChattingRoom;
import com.usw.sugo.domain.majorchatting.ChattingRoomFile;
import com.usw.sugo.domain.majorchatting.ChattingRoomMessage;
import com.usw.sugo.domain.majorchatting.chattingRoom.repository.ChattingRoomRepository;
import com.usw.sugo.domain.majorchatting.chattingRoomMessaging.dto.ChattingMessage;
import com.usw.sugo.domain.majorchatting.chattingRoomMessaging.redisRepository.RedisChattingMessageRepository;
import com.usw.sugo.domain.majorchatting.chattingRoomMessaging.repository.ChattingRoomFileRepository;
import com.usw.sugo.domain.majorchatting.chattingRoomMessaging.repository.ChattingRoomMessageRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import com.usw.sugo.global.redis.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChattingRoomMessagingController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final UserRepository userRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingRoomMessageRepository messageRepository;
    private final ChattingRoomFileRepository fileRepository;
    private final RedisChattingMessageRepository redisChattingMessageRepository;
    private final AmazonS3Client amazonS3Client;
    private final RedisPublisher redisPublisher;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChattingMessage message) {
        if (ChattingMessage.MessageType.MESSAGE.equals(message.getType())) {
            ChattingRoom requestChattingRoom = chattingRoomRepository.findById(message.getChattingRoomId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CHATTING_ROOM_NOT_FOUND));

            User sender = userRepository.findById(message.getSenderId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

            User receiver = userRepository.findById(message.getSenderId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

            ChattingRoomMessage chattingRoomMessage = ChattingRoomMessage.builder()
                    .chattingRoomId(requestChattingRoom)
                    .sender(sender)
                    .receiver(receiver)
                    .message(message.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();

            messageRepository.save(chattingRoomMessage);

            message.setMessage(message.getMessage());
        }
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(redisChattingMessageRepository.getTopic(message.getUuid()), message);
    }

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/file")
    public void file(ChattingMessage message) throws IOException {
        if (ChattingMessage.MessageType.FILE.equals(message.getType())) {

            ChattingRoom requestChattingRoom = chattingRoomRepository.findById(message.getChattingRoomId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CHATTING_ROOM_NOT_FOUND));

            User sender = userRepository.findById(message.getSenderId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

            User receiver = userRepository.findById(message.getSenderId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

            // 게시글 이미지 링크를 담을 리스트
            List<String> imagePathList = new ArrayList<>();

            for (MultipartFile multipartFile : message.getMultipartFileList()) {
                // 파일 이름
                String originalName = multipartFile.getOriginalFilename();

                // 파일 크기
                long size = multipartFile.getSize();

                ObjectMetadata objectMetaData = new ObjectMetadata();
                objectMetaData.setContentType(multipartFile.getContentType());
                objectMetaData.setContentLength(size);

                // S3에 업로드
                amazonS3Client.putObject(
                        new PutObjectRequest(bucket + "/chatting-resource", originalName, multipartFile.getInputStream(), objectMetaData)
                                .withCannedAcl(CannedAccessControlList.PublicRead));

                // S3 링크 DB에 넣을 준비 -> 접근가능한 URL 가져오기
                String imagePath = amazonS3Client.getUrl(bucket + "/chatting-resource", originalName).toString();
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

            ChattingRoomFile chattingRoomFile = ChattingRoomFile.builder()
                    .chattingRoomId(requestChattingRoom)
                    .imageLink(String.valueOf(sb))
                    .sender(sender)
                    .receiver(receiver)
                    .createdAt(LocalDateTime.now())
                    .build();

            // 채팅방 이미지 저장
            fileRepository.save(chattingRoomFile);
            message.setMessage(sb.toString());
        }
        // Websocket 에 발행된 메시지를 redis 로 발행한다(publish)
        redisPublisher.publish(redisChattingMessageRepository.getTopic(message.getUuid()), message);
    }
}
