package com.usw.sugo.domain.user.user.controller;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserRequestDto;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.*;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @ResponseStatus(OK)
    @PostMapping("/check-loginId")
    public Map<String, Boolean> checkLoginId(
            @Valid @RequestBody IsLoginIdExistRequestForm isLoginIdExistRequestForm) {
        return userService.executeIsLoginIdExist(isLoginIdExistRequestForm.getLoginId());
    }

    @ResponseStatus(OK)
    @PostMapping("/check-email")
    public Map<String, Boolean> checkEmail(
            @Valid @RequestBody IsEmailExistRequestForm isEmailExistRequestForm) {
        return userService.executeIsEmailExist(isEmailExistRequestForm.getEmail());
    }

    @ResponseStatus(OK)
    @PostMapping("/find-id")
    public Map<String, Boolean> findId(
            @Valid @RequestBody FindLoginIdRequestForm findLoginIdRequestForm) {
        return userService.executeFindLoginId(findLoginIdRequestForm.getEmail());
    }

    @ResponseStatus(OK)
    @PostMapping("/find-pw")
    public Map<String, Boolean> sendPasswordEmail(
            @Valid @RequestBody FindPasswordRequestForm findPasswordRequestForm,
            @AuthenticationPrincipal User user) {
        return userService.executeFindPassword(
                findPasswordRequestForm.getEmail(),
                user);
    }

    @ResponseStatus(OK)
    @PostMapping("/join")
    public Map<String, Object> detailJoin(
            @Valid @RequestBody DetailJoinRequestForm detailJoinRequestForm) {
        return userService.executeJoin(
                detailJoinRequestForm.getLoginId(),
                detailJoinRequestForm.getEmail(),
                detailJoinRequestForm.getPassword(),
                detailJoinRequestForm.getDepartment()
        );
    }

    @PostMapping("/auth")
    public Map<String, Boolean> confirmEmail(
            @RequestBody AuthEmailPayloadForm authEmailPayloadForm) {
        return userService.executeAuthEmailPayload(
                authEmailPayloadForm.getPayload(),
                authEmailPayloadForm.getUserId()
        );
    }

    // 비밀번호 수정
    @ResponseStatus(OK)
    @PutMapping("/password")
    public Map<String, Boolean> editPassword(
            @RequestHeader String authorization,
            @Valid @RequestBody EditPasswordRequestForm editPasswordRequestForm,
            @AuthenticationPrincipal User user) {
        return userService.executeEditPassword(
                user, editPasswordRequestForm.getPrePassword(), editPasswordRequestForm.getNewPassword());
    }

    @ResponseStatus(OK)
    @DeleteMapping
    public Map<String, Boolean> deleteUser(
            @RequestHeader String authorization,
            @Valid @RequestBody UserRequestDto.QuitRequestForm quitRequestForm,
            @AuthenticationPrincipal User user) {
        return userService.executeQuit(user);
    }

    @ResponseStatus(OK)
    @GetMapping("/identifier")
    public Map<String, Long> getMyIndex(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user) {
        return new HashMap<>() {{
            put("userId", user.getId());
        }};
    }

    @ResponseStatus(OK)
    @GetMapping
    public UserPageResponseForm loadMyPage(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return userService.executeLoadUserPage(user, user.getId(), pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/{userId}")
    public UserPageResponseForm loadOtherUserPage(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            Pageable pageable) {
        return userService.executeLoadUserPage(user, userId, pageable);
    }

    @ResponseStatus(OK)
    @PostMapping("/manner")
    public Map<String, Boolean> evaluateManner(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MannerEvaluationRequestForm mannerEvaluationRequestForm) {
        return userService.executeEvaluateManner(
                mannerEvaluationRequestForm.getTargetUserId(),
                mannerEvaluationRequestForm.getGrade(),
                user
        );
    }
}
