package com.usw.sugo.domain.majorproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDto {

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MainPageResponse {
        // 게시글 인덱스
        private Long id;
        // 이미지 링크
        private String imageLink;
        // 거래 장소
        private String contactPlace;
        // 게시글 최종 수정 시각
        private LocalDateTime updatedAt;
        // 상품 게시글 제목
        private String title;
        // 상품 가격
        private Integer price;
        // 상품 게시글 작성자
        private String nickname;
        // 게시글 카테고리
        private String category;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostFileDomainResponse {
        // 이미지 링크
        public List<String> imageLink;
    }
}
