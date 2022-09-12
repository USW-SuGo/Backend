package com.usw.sugo.domain.majoruser.user.dto;

import lombok.Data;

public class UserRequestDto {

    @Data
    public static class IsEmailExistRequest {
        String email;
    }

    @Data
    public static class SendAuthorizationEmailRequest {
        String email;
    }

    // 인증받은 이메일과 비밀번호를 입력받기
    @Data
    public static class DetailJoinRequest {
        String email;
        String password;
        String department;
    }

    @Data
    public static class EditPasswordRequest {
        Long id;
        String password;
    }

    @Data
    public static class EditNicknameRequest {
        Long id;
        String nickname;
    }
}
