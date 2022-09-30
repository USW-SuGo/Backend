package com.usw.sugo.domain.majorchatting.chattingRoom.controller;

import com.usw.sugo.domain.majorchatting.chattingRoom.dto.ChatRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.dto.FileRequest;
import com.usw.sugo.domain.majorchatting.chattingRoom.service.ChattingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class ChattingController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomService chattingRoomService;

    // 메세지 전송
    @MessageMapping("/messages")
    public void chat(@Valid ChatRequest chatRequest) {
        chattingRoomService.saveMessages(chatRequest);
        simpMessagingTemplate.convertAndSend("/subscribe/rooms/"
                + chatRequest.getRoomId(), chatRequest.getMessage());
    }

    // 이미지 전송
    @MessageMapping("/files")
    public void postFile(@Valid FileRequest fileRequest) throws IOException {
        chattingRoomService.saveFiles(fileRequest);
        simpMessagingTemplate.convertAndSend("/subscribe/rooms/"
                + fileRequest.getRoomId(), fileRequest.getMultipartFileList());
    }
}
