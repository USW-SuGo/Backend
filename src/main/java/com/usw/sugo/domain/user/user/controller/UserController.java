package com.usw.sugo.domain.user.user.controller;

import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.AuthEmailPayloadForm;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.DetailJoinRequestForm;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.EditPasswordRequestForm;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.FindLoginIdRequestForm;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.FindPasswordRequestForm;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.IsEmailExistRequestForm;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.IsLoginIdExistRequestForm;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.MannerEvaluationRequestForm;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.QuitRequestForm;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.user.service.UserService;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
            detailJoinRequestForm.getDepartment(),
            detailJoinRequestForm.getPushAlarmStatus(),
            detailJoinRequestForm.getFcmToken()
        );
    }

    @PostMapping("/auth")
    public Map<String, Boolean> confirmEmail(
        @Valid @RequestBody AuthEmailPayloadForm authEmailPayloadForm) {
        return userService.executeAuthEmailPayload(
            authEmailPayloadForm.getPayload(),
            authEmailPayloadForm.getUserId()
        );
    }

    // 비밀번호 수정
    @ResponseStatus(OK)
    @PutMapping("/password")
    public Map<String, Boolean> editPassword(
        @Valid @RequestBody EditPasswordRequestForm editPasswordRequestForm,
        @AuthenticationPrincipal User user) {
        return userService.executeEditPassword(user, editPasswordRequestForm.getNewPassword());
    }

    @ResponseStatus(OK)
    @DeleteMapping
    public Map<String, Boolean> deleteUser(@Valid @RequestBody QuitRequestForm quitRequestForm,
        @AuthenticationPrincipal User user) {
        return userService.executeQuit(user, quitRequestForm.getPassword());
    }

    @ResponseStatus(OK)
    @GetMapping("/identifier")
    public Map<String, Long> getMyIndex(@AuthenticationPrincipal User user) {
        return new HashMap<>() {{
            put("userId", user.getId());
        }};
    }

    @ResponseStatus(OK)
    @GetMapping
    public UserPageResponseForm loadMyPage(@AuthenticationPrincipal User user) {
        return userService.executeLoadUserPage(user, user.getId());
    }

    @ResponseStatus(OK)
    @GetMapping("/{userId}")
    public UserPageResponseForm loadOtherUserPage(@PathVariable Long userId,
        @AuthenticationPrincipal User user) {
        return userService.executeLoadUserPage(user, userId);
    }

    @ResponseStatus(OK)
    @PostMapping("/manner")
    public Map<String, Boolean> evaluateManner(
        @Valid @RequestBody MannerEvaluationRequestForm mannerEvaluationRequestForm,
        @AuthenticationPrincipal User user) {
        return userService.executeEvaluateManner(
            mannerEvaluationRequestForm.getTargetUserId(),
            mannerEvaluationRequestForm.getGrade(),
            user
        );
    }
}
