package com.usw.sugo.domain.majorchatting.chattingRoomMessaging.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class ChattingMessage implements Serializable {

    public enum MessageType{
        ENTER, MESSAGE, FILE, OUT
    }

    private MessageType type;
    private long chattingRoomId;
    private String uuid;
    private String message;
    private MultipartFile[] multipartFileList;
    private long senderId;
    private long receiverId;

}
