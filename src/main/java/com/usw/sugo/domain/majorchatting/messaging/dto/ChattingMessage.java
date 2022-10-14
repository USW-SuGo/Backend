package com.usw.sugo.domain.majorchatting.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChattingMessage implements Serializable {

    private String type;
    private long chattingRoomId;
    private String uuid;
    private String message;
    private MultipartFile[] multipartFileList;
    private long senderId;
    private long receiverId;
}
