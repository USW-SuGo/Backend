package com.usw.sugo.global.fcm.controller;

import static com.usw.sugo.global.apiresult.ApiResultFactory.getSuccessFlag;
import static org.springframework.http.HttpStatus.OK;

import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.service.UserServiceUtility;
import com.usw.sugo.global.fcm.controller.dto.FcmRequestDto.UpdateFcmTokenForm;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {

    private final UserServiceUtility userServiceUtility;

    @ResponseStatus(OK)
    @PostMapping
    public Map<String, Boolean> updateFcmToken(
        @RequestBody UpdateFcmTokenForm updateFcmTokenForm,
        @AuthenticationPrincipal User user
    ) {
        userServiceUtility.loadUserById(
            user.getId()
        ).updateFcmToken(updateFcmTokenForm.getFcmToken());
        return getSuccessFlag();
    }
}