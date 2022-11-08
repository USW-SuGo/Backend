package com.usw.sugo.domain.chatting.messaging.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.usw.sugo.domain.chatting.ChattingRoom;
import com.usw.sugo.domain.chatting.ChattingRoomFile;
import com.usw.sugo.domain.chatting.ChattingRoomMessage;
import com.usw.sugo.domain.chatting.room.repository.ChattingRoomRepository;
import com.usw.sugo.domain.chatting.messaging.dto.ChattingMessage;
import com.usw.sugo.domain.chatting.messaging.repository.ChattingRoomFileRepository;
import com.usw.sugo.domain.chatting.messaging.repository.ChattingRoomMessageRepository;
import com.usw.sugo.domain.productpost.ProductPost;
import com.usw.sugo.domain.productpost.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.User;
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
public class ChattingRoomMessagingService {

    private final UserRepository userRepository;
    private final ProductPostRepository productPostRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingRoomMessageRepository messageRepository;
    private final ChattingRoomFileRepository fileRepository;

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    // 채팅방 개설
    public void createChattingRoom(ChattingMessage message, long productPostId) {

        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_BAD_REQUEST));

        User seller = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        User buyer = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        ChattingRoom chattingRoom = ChattingRoom.builder()
                .productPost(productPost)
                .sellerId(seller)
                .buyerId(buyer)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        chattingRoomRepository.save(chattingRoom);

        // 거래 시도 횟수 + 1
        userRepository.plusCountTradeAttempt(seller.getId(), buyer.getId());
    }

    // 메세지 DB 저장
    public ChattingMessage uploadMessageByMessaging(ChattingMessage message, long chattingRoomId) {
        // 메세지를 DB에 저장하는 로직 시작
        ChattingRoom requestChattingRoom = chattingRoomRepository.findById(chattingRoomId)
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

        // 메세지를 DB에 저장하는 로직 종료

        return message;
    }

    // 파일 DB 저장
    public ChattingMessage uploadFileByMessaging(ChattingMessage message, long chattingRoomId) throws IOException {
        // 파일을 DB에 저장하는 로직 시작

        ChattingRoom requestChattingRoom = chattingRoomRepository.findById(chattingRoomId)
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
        // 파일을 DB에 저장하는 로직 종료
        return message;
    }
}
