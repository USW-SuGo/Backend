package com.usw.sugo.domain.user.user.controller;

import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.AuthEmailPayloadForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.DetailJoinRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.EditPasswordRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.FindLoginIdRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.FindPasswordRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.IsEmailExistRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.IsLoginIdExistRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.MannerEvaluationRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.PushAlarmStatusRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.QuitRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserRequestDto.RegisterFcmTokenRequestForm;
import com.usw.sugo.domain.user.user.controller.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.user.service.UserService;
import com.usw.sugo.global.annotation.ApiLogger;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @ApiLogger
    @ResponseStatus(OK)
    @PostMapping("/check-loginId")
    public Map<String, Boolean> checkLoginId(
        @Valid @RequestBody IsLoginIdExistRequestForm isLoginIdExistRequestForm) {
        return userService.executeIsLoginIdExist(isLoginIdExistRequestForm.getLoginId());
    }

    @ApiLogger
    @ResponseStatus(OK)
    @PostMapping("/check-email")
    public Map<String, Boolean> checkEmail(
        @Valid @RequestBody IsEmailExistRequestForm isEmailExistRequestForm
    ) {
        return userService.executeIsEmailExist(isEmailExistRequestForm.getEmail());
    }

    @ApiLogger
    @ResponseStatus(OK)
    @PostMapping("/find-id")
    public Map<String, Boolean> findId(
        @Valid @RequestBody FindLoginIdRequestForm findLoginIdRequestForm
    ) {
        return userService.executeFindLoginId(findLoginIdRequestForm.getEmail());
    }

    @ApiLogger
    @ResponseStatus(OK)
    @PostMapping("/find-pw")
    public Map<String, Boolean> sendPasswordEmail(
        @Valid @RequestBody FindPasswordRequestForm findPasswordRequestForm,
        @AuthenticationPrincipal User user
    ) {
        return userService.executeFindPassword(
            findPasswordRequestForm.getEmail(),
            user);
    }

    @ApiLogger
    @ResponseStatus(OK)
    @PostMapping("/join")
    public Map<String, Object> detailJoin(
        @Valid @RequestBody DetailJoinRequestForm detailJoinRequestForm
    ) {
        return userService.executeJoin(
            detailJoinRequestForm.getLoginId(),
            detailJoinRequestForm.getEmail(),
            detailJoinRequestForm.getPassword(),
            detailJoinRequestForm.getDepartment()
        );
    }

    @ApiLogger
    @PostMapping("/auth")
    public Map<String, Boolean> confirmEmail(
        @Valid @RequestBody AuthEmailPayloadForm authEmailPayloadForm
    ) {
        return userService.executeAuthEmailPayload(
            authEmailPayloadForm.getPayload(),
            authEmailPayloadForm.getUserId()
        );
    }

    @ApiLogger
    @ResponseStatus(OK)
    @PutMapping("/password")
    public Map<String, Boolean> editPassword(
        @Valid @RequestBody EditPasswordRequestForm editPasswordRequestForm,
        @AuthenticationPrincipal User user) {
        return userService.executeEditPassword(user, editPasswordRequestForm.getNewPassword());
    }

    @ApiLogger
    @ResponseStatus(OK)
    @DeleteMapping
    public Map<String, Boolean> deleteUser(
        @Valid @RequestBody QuitRequestForm quitRequestForm,
        @AuthenticationPrincipal User user
    ) {
        return userService.executeQuit(user, quitRequestForm.getPassword());
    }

    @ApiLogger
    @ResponseStatus(OK)
    @GetMapping("/identifier")
    public Map<String, Long> getMyIndex(@AuthenticationPrincipal User user) {
        return new HashMap<>() {{
            put("userId", user.getId());
        }};
    }

    @ApiLogger
    @ResponseStatus(OK)
    @GetMapping
    public UserPageResponseForm loadMyPage(@AuthenticationPrincipal User user) {
        return userService.executeLoadUserPage(user, user.getId());
    }

    @ApiLogger
    @ResponseStatus(OK)
    @GetMapping("/{userId}")
    public UserPageResponseForm loadOtherUserPage(
        @PathVariable Long userId,
        @AuthenticationPrincipal User user
    ) {
        return userService.executeLoadUserPage(user, userId);
    }

    @ApiLogger
    @ResponseStatus(OK)
    @PostMapping("/manner")
    public Map<String, Boolean> evaluateManner(
        @Valid @RequestBody MannerEvaluationRequestForm mannerEvaluationRequestForm,
        @AuthenticationPrincipal User user
    ) {
        return userService.executeEvaluateManner(
            mannerEvaluationRequestForm.getTargetUserId(),
            mannerEvaluationRequestForm.getGrade(),
            user
        );
    }

    @ApiLogger
    @ResponseStatus(OK)
    @PatchMapping("/alarm-status")
    public Map<String, Boolean> updatePushAlarmStatus(
        @Valid @RequestBody PushAlarmStatusRequestForm pushAlarmStatusRequestForm,
        @AuthenticationPrincipal User user
    ) {
        return userService.executeUpdatePushAlarmStatus(
            pushAlarmStatusRequestForm.getPushAlarmStatus(), user
        );
    }

    @ApiLogger
    @ResponseStatus(OK)
    @PatchMapping("/fcm")
    public Map<String, Boolean> updateFcmToken(
        @Valid @RequestBody RegisterFcmTokenRequestForm registerFcmTokenRequestForm,
        @AuthenticationPrincipal User user
    ) {
        return userService.executeUpdateFcmToken(
            registerFcmTokenRequestForm.getFcmToken(), user
        );
    }
}
