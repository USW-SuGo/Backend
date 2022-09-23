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
        public Long id;
        // 이미지 링크
        public String imageLink;
        // 거래 장소
        public String contactPlace;
        // 게시글 최종 수정 시각
        public LocalDateTime updatedAt;
        // 상품 게시글 제목
        public String title;
        // 상품 가격
        public Integer price;
        // 상품 게시글 작성자
        public String nickname;
        // 게시글 카테고리
        public String category;
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
