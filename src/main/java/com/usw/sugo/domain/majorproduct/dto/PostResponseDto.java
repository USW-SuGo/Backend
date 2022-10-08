package com.usw.sugo.domain.majorproduct.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class PostResponseDto {

    @Data
    public static class SearchResultResponse {
        // 게시글 인덱스
        private long id;
        // 이미지 링크
        private String imageLink;
        // 거래 장소
        private String contactPlace;
        // 게시글 최종 수정 시각
        private LocalDateTime updatedAt;
        // 상품 게시글 제목
        private String title;
        // 상품 가격
        private int price;
        // 상품 게시글 작성자
        private String nickname;
        // 게시글 카테고리
        private String category;
    }

    // 전체 포스트 조회에 대한 DTO
    @Data
    public static class MainPageResponse {
        // 게시글 인덱스
        private long id;
        // 이미지 링크
        private String imageLink;
        // 거래 장소
        private String contactPlace;
        // 게시글 최종 수정 시각
        private LocalDateTime updatedAt;
        // 상품 게시글 제목
        private String title;
        // 상품 가격
        private int price;
        // 상품 게시글 작성자
        private String nickname;
        // 게시글 카테고리
        private String category;
    }

    // 특정 게시물 조회에 대한 반환 DTO
    @Data
    public static class DetailPostResponse {
        // 게시글 인덱스
        private long id;
        // 이미지 링크
        private String imageLink;
        // 거래 장소
        private String contactPlace;
        // 게시글 최종 수정 시각
        private LocalDateTime updatedAt;
        // 상품 게시글 제목
        private String title;
        // 상품 가격
        private int price;
        // 상품 게시글 작성자
        private String nickname;
        // 게시글 카테고리
        private String category;
    }
}
