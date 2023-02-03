package com.usw.sugo.domain.user.user.controller;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserService;
import com.usw.sugo.domain.user.user.service.UserServiceCluster;
import com.usw.sugo.domain.user.user.dto.UserRequestDto;
import com.usw.sugo.domain.user.user.dto.UserResponseDto;
import com.usw.sugo.global.jwt.JwtResolver;
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

    private final UserControllerValidator userControllerValidator;
    private final UserServiceCluster userServiceCluster;

    // ------------- 임시 의존성 ----------------//
    private final UserService userService;
    private final JwtResolver jwtResolver;
    // ------------- 임시 의존성 ----------------//


    @ResponseStatus(OK)
    @PostMapping("/check-loginId")
    public Map<String, Boolean> checkLoginId(
            @Valid @RequestBody UserRequestDto.IsLoginIdExistRequestForm isLoginIdExistRequestForm) {
        return userServiceCluster.executeIsLoginIdExist(isLoginIdExistRequestForm);
    }

    @ResponseStatus(OK)
    @PostMapping("/check-email")
    public Map<String, Boolean> checkEmail(
            @Valid @RequestBody UserRequestDto.IsEmailExistRequestForm isEmailExistRequestForm) {
        return userServiceCluster.executeIsEmailExist(isEmailExistRequestForm);
    }

    @ResponseStatus(OK)
    @PostMapping("/find-id")
    public Map<String, Boolean> findId(
            @Valid @RequestBody UserRequestDto.FindLoginIdRequestForm findLoginIdRequestForm) {
        return userServiceCluster.executeFindLoginId(
                findLoginIdRequestForm,
                userControllerValidator.validateUserByEmail(findLoginIdRequestForm.getEmail()));
    }

    @ResponseStatus(OK)
    @PostMapping("/find-pw")
    public Map<String, Boolean> sendPasswordEmail(
            @Valid @RequestBody UserRequestDto.FindPasswordRequestForm findPasswordRequestForm,
            @AuthenticationPrincipal User user) {
        return userServiceCluster.executeFindPassword(
                findPasswordRequestForm,
                userControllerValidator.validateUserByEmail(findPasswordRequestForm.getEmail()));
    }

    @ResponseStatus(OK)
    @PostMapping("/join")
    public Map<String, Object> detailJoin(
            @Valid @RequestBody UserRequestDto.DetailJoinRequestForm detailJoinRequestForm) {
        return userServiceCluster.executeJoin(detailJoinRequestForm);
    }

    @PostMapping("/auth")
    public Map<String, Boolean> confirmEmail(
            @RequestBody UserRequestDto.AuthEmailPayloadForm authEmailPayloadForm) {
        userControllerValidator.validateUserEmailAuth(authEmailPayloadForm.getUserId());
        return userServiceCluster.executeAuthEmailPayload(authEmailPayloadForm);
    }

    // 비밀번호 수정
    @ResponseStatus(OK)
    @PutMapping("/password")
    public Map<String, Boolean> editPassword(
            @RequestHeader String authorization,
            @Valid @RequestBody UserRequestDto.EditPasswordRequestForm editPasswordRequestForm,
            @AuthenticationPrincipal User user) {
        userControllerValidator.validatePasswordForEditPassword(editPasswordRequestForm.getId(),
                editPasswordRequestForm.getPassword());

        userService.loadUserByLoginId(jwtResolver.jwtResolveToUserLoginId(authorization.substring(7)));

        return userServiceCluster.executeEditPassword(editPasswordRequestForm, user);
    }

    @ResponseStatus(OK)
    @DeleteMapping
    public Map<String, Boolean> deleteUser(
            @RequestHeader String authorization,
            @Valid @RequestBody UserRequestDto.QuitRequestForm quitRequestForm,
            @AuthenticationPrincipal User user) {
        userControllerValidator.validatePasswordForAuthorization(user.getId(), user.getPassword());
        return userServiceCluster.executeQuit(quitRequestForm, user);
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
    public UserResponseDto.UserPageResponseForm loadMyPage(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            Pageable pageable) {

        // 임시 코드 --------------------------------------------------------------
        Long userId = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        return userServiceCluster.executeLoadUserPage(userService.loadUserById(userId), userId, pageable);
    }

    @ResponseStatus(OK)
    @GetMapping("/{userId}")
    public UserResponseDto.UserPageResponseForm loadOtherUserPage(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            Pageable pageable) {
        // 임시 코드 --------------------------------------------------------------
        Long userIdx = jwtResolver.jwtResolveToUserId(authorization.substring(7));
        return userServiceCluster.executeLoadUserPage(userService.loadUserById(userIdx), userId, pageable);
    }

    @ResponseStatus(OK)
    @PostMapping("/manner")
    public Map<String, Boolean> evaluateManner(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserRequestDto.MannerEvaluationRequestForm mannerEvaluationRequestForm) {
        userControllerValidator.validateUserById(user.getId());
        userControllerValidator.validateUserById(mannerEvaluationRequestForm.getTargetUserId());
        return userServiceCluster.executeEvaluateManner(mannerEvaluationRequestForm, user);
    }
}
