package com.usw.sugo.domain.user.user.service;

import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import com.usw.sugo.domain.user.useremailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.user.userlikepost.service.UserLikePostService;
import com.usw.sugo.global.aws.ses.SendEmailServiceBySES;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.util.nickname.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.usw.sugo.domain.ApiResult.EXIST;
import static com.usw.sugo.domain.ApiResult.SUCCESS;
import static com.usw.sugo.global.exception.ExceptionType.ALREADY_EVALUATION;
import static com.usw.sugo.global.exception.ExceptionType.PAYLOAD_NOT_VALID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceCluster {
    private final UserService userService;
    private final SendEmailServiceBySES sendEmailServiceBySES;
    private final UserEmailAuthService userEmailAuthService;
    private final NoteService noteService;
    private final ProductPostService productPostService;
    private final UserLikePostService userLikePostService;

    private static final Map<String, Boolean> overlapFlag = new HashMap<>() {{
        put(EXIST.getResult(), true);
    }};
    private static final Map<String, Boolean> unOverlapFlag = new HashMap<>() {{
        put(EXIST.getResult(), false);
    }};
    private static final Map<String, Boolean> successFlag = new HashMap<>() {{
        put(SUCCESS.getResult(), true);
    }};
    private static final Map<String, Boolean> failFlag = new HashMap<>() {{
        put(SUCCESS.getResult(), false);
    }};


    public Map<String, Boolean> executeIsLoginIdExist(String loginId) {
        if (userService.validateLoginIdDuplicated(loginId)) {
            return overlapFlag;
        }
        return unOverlapFlag;
    }

    public Map<String, Boolean> executeIsEmailExist(String email) {
        userService.validateSuwonUniversityEmailForm(email);
        if (userService.validateEmailDuplicated(email)) {
            return overlapFlag;
        }
        return unOverlapFlag;
    }

    public Map<String, Boolean> executeFindLoginId(String email) {
        sendEmailServiceBySES.sendFindLoginIdResult(email, userService.loadUserByEmail(email).getLoginId());
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> executeFindPassword(String email, User user) {
        String newPassword = userService.initPassword(user);
        sendEmailServiceBySES.sendFindPasswordResult(email, newPassword);
        return successFlag;
    }

    @Transactional
    public Map<String, Object> executeJoin(String loginId, String email, String password, String department) {
        userService.validateLoginIdDuplicated(loginId);
        userService.validateSuwonUniversityEmailForm(email);
        userService.validateEmailDuplicated(email);
        User requestUser = userService.softJoin(loginId, email, password);
        requestUser.updateNickname(NicknameGenerator.generateNickname(department));

        String authPayload = userEmailAuthService.saveUserEmailAuth(requestUser);
        sendEmailServiceBySES.sendStudentAuthContent(requestUser.getEmail(), authPayload);
        return new HashMap<>() {{
            put(SUCCESS.getResult(), true);
            put("id", requestUser.getId());
        }};
    }

    @Transactional
    public Map<String, Boolean> executeAuthEmailPayload(String payload, Long userId) {
        UserEmailAuth requestUserEmailAuth = userEmailAuthService.loadUserEmailAuthByUser(
                userService.loadUserById(userId));

        if (requestUserEmailAuth.getPayload().equals(payload)) {
            User requestUser = requestUserEmailAuth.getUser();
            requestUserEmailAuth.confirmToken();
            requestUser.encryptPassword(requestUser.getPassword());
            requestUser.modifyingStatusToAvailable();
            userEmailAuthService.deleteConfirmedEmailAuthByUser(requestUser);
            return successFlag;
        }
        throw new CustomException(PAYLOAD_NOT_VALID);
    }

    @Transactional
    public Map<String, Boolean> executeEditPassword(User user, String newPassword) {
        user.encryptPassword(newPassword);
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> executeQuit(User user) {
        noteService.deleteNoteByUser(user);
        productPostService.deleteByUser(user);
        userService.deleteUser(user);
        return successFlag;
    }

    public UserPageResponseForm executeLoadUserPage(User user, Long userId, Pageable pageable) {
        if (user.getId().equals(userId)) {
            return UserPageResponseForm.builder()
                    .userId(userId)
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .mannerGrade(user.getMannerGrade())
                    .countMannerEvaluation(user.getCountMannerEvaluation())
                    .countTradeAttempt(user.getCountTradeAttempt())
                    .myPostings(productPostService.loadUserWritingPostingList(user, pageable))
                    .likePostings(userLikePostService.loadLikePosts(user.getId()))
                    .build();
        }
        return UserPageResponseForm.builder()
                .userId(userId)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .mannerGrade(user.getMannerGrade())
                .countMannerEvaluation(user.getCountMannerEvaluation())
                .countTradeAttempt(user.getCountTradeAttempt())
                .myPostings(productPostService.loadUserWritingPostingList(user, pageable))
                .build();
    }

    @Transactional
    public Map<String, Boolean> executeEvaluateManner(Long targetUserId, BigDecimal grade, User user) {
        if (userService.isBeforeDay(user.getRecentEvaluationManner())) {
            userService.loadUserById(targetUserId).updateMannerGrade(grade);
            user.updateRecentEvaluationManner();
            return successFlag;
        }
        throw new CustomException(ALREADY_EVALUATION);
    }
}
