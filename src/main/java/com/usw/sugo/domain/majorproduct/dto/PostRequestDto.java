package com.usw.sugo.domain.majorproduct.dto;

import lombok.Getter;
import lombok.Setter;


public class PostRequestDto {

    @Getter @Setter
    public static class PostRequest {
        private String title;
        private String content;
        private int price;
        private String contactPlace;
        private String category;
    }

}
