package com.usw.sugo.domain.majorproduct.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public class PostRequestDto {

    @Getter @Setter
    public static class PostRequest implements Serializable {
        private long userId;
        private String title;
        private String content;
        private int price;
        private String contactPlace;
        private String category;
        private MultipartFile image;
    }

}
