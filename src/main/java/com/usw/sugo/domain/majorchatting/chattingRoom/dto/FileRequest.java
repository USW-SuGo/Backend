package com.usw.sugo.domain.majorchatting.chattingRoom.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FileRequest {

    /**
     * 송신자 id
     */
    @NotNull
    private Long senderId;

    /**
     * 수신자 id
     */
    @NotNull
    private Long receiverId;

    /**
     * 채팅방 id
     */
    @NotNull
    private Long roomId;

    /**
     * 파일 내용
     */
    @NotBlank
    MultipartFile[] multipartFileList;

    public FileRequest(Long senderId, Long receiverId, Long roomId, MultipartFile[] multipartFileList) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.roomId = roomId;
        this.multipartFileList = multipartFileList;
    }
}
