package com.usw.sugo.domain.user.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class UserResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPageResponseForm {
        private Long userId;
        private String email;
        private String nickname;
        private BigDecimal mannerGrade;
        private Long countMannerEvaluation;
        private Long countTradeAttempt;
    }
}
