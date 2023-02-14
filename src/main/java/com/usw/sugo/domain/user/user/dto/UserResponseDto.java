package com.usw.sugo.domain.user.user.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
