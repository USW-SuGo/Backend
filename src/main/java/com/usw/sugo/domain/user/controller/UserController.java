package com.usw.sugo.domain.user.controller;

import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.dto.UserRequestDto.*;
import com.usw.sugo.domain.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.service.UserServiceCluster;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/check-loginId")
    public ResponseEntity<Map<String, Boolean>> checkLoginId(
            @Valid @RequestBody IsLoginIdExistRequestForm isLoginIdExistRequestForm) {
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeIsLoginIdExist(isLoginIdExistRequestForm));
    }

    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(
            @Valid @RequestBody IsEmailExistRequestForm isEmailExistRequestForm) {
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeIsEmailExist(isEmailExistRequestForm));
    }

    @PostMapping("/find-id")
    public ResponseEntity<Map<String, Boolean>> findId(
            @Valid @RequestBody FindLoginIdRequestForm findLoginIdRequestForm) {
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeFindLoginId(
                        findLoginIdRequestForm,
                        userControllerValidator.validateUserByEmail(findLoginIdRequestForm.getEmail())));
    }

    @PostMapping("/find-pw")
    public ResponseEntity<Map<String, Boolean>> sendPasswordEmail(
            @Valid @RequestBody FindPasswordRequestForm findPasswordRequestForm,
            @AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeFindPassword(
                        findPasswordRequestForm,
                        userControllerValidator.validateUserByEmail(findPasswordRequestForm.getEmail())));
    }

    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> detailJoin(
            @Valid @RequestBody DetailJoinRequestForm detailJoinRequestForm) {
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeJoin(detailJoinRequestForm));
    }

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Boolean>> confirmEmail(
            @RequestBody AuthEmailPayloadForm authEmailPayloadForm) {
        userControllerValidator.validateUserEmailAuth(authEmailPayloadForm.getUserId());
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeAuthEmailPayload(authEmailPayloadForm));
    }

    // 비밀번호 수정
    @PutMapping("/password")
    public ResponseEntity<Map<String, Boolean>> editPassword(
            @Valid @RequestBody EditPasswordRequestForm editPasswordRequestForm,
            @AuthenticationPrincipal User user) {
        userControllerValidator.validatePasswordForEditPassword(editPasswordRequestForm.getId(),
                editPasswordRequestForm.getPassword());
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeEditPassword(editPasswordRequestForm, user));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> deleteUser(
            @RequestHeader String authorization,
            @Valid @RequestBody QuitRequestForm quitRequestForm,
            @AuthenticationPrincipal User user) {
        userControllerValidator.validatePasswordForAuthorization(user.getId(), user.getPassword());
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeQuit(quitRequestForm, user));
    }

    @GetMapping("/identifier")
    public ResponseEntity<Map<String, Long>> getMyIndex(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("userId", user.getId());
                }});

    }

    @GetMapping
    public ResponseEntity<UserPageResponseForm> loadMyPage(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            Pageable pageable,
            HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeLoadUserPage(user, pageable, null));
    }

    @GetMapping("/")
    public ResponseEntity<UserPageResponseForm> loadOtherUserPage(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            @PathVariable Long userId,
            Pageable pageable,
            HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(OK)
                .body(userServiceCluster.executeLoadUserPage(user, pageable, httpServletRequest.getPathInfo()));
    }

    @PostMapping("/manner")
    public ResponseEntity<?> evaluateManner(
            @RequestHeader String authorization,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MannerEvaluationRequestForm mannerEvaluationRequestForm) {
        userControllerValidator.validateUserById(user.getId());
        userControllerValidator.validateUserById(mannerEvaluationRequestForm.getTargetUserId());
        return ResponseEntity.status(OK)
                .body(userServiceCluster.executeEvaluateManner(mannerEvaluationRequestForm, user));
    }
}
