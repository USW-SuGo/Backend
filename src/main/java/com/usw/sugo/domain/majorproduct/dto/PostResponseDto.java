package com.usw.sugo.domain.majorproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostResponseDto {

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainPageResponseForm {
        public String imageLink;
        public String postTitle;
        public String contactPlace;
    }

}
