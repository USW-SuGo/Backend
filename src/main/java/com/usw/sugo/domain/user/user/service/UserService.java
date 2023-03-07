package com.usw.sugo.domain.user.user.service;

import static com.usw.sugo.global.valueobject.apiresult.ApiResult.SUCCESS;
import static com.usw.sugo.global.valueobject.apiresult.ApiResultFactory.getExistFlag;
import static com.usw.sugo.global.valueobject.apiresult.ApiResultFactory.getNotExistFlag;
import static com.usw.sugo.global.valueobject.apiresult.ApiResultFactory.getSuccessFlag;
import static com.usw.sugo.global.exception.ExceptionType.ALREADY_EVALUATION;
import static com.usw.sugo.global.exception.ExceptionType.DUPLICATED_EMAIL;
import static com.usw.sugo.global.exception.ExceptionType.DUPLICATED_LOGINID;
import static com.usw.sugo.global.exception.ExceptionType.IS_SAME_PASSWORD;
import static com.usw.sugo.global.exception.ExceptionType.PASSWORD_NOT_CORRECT;
import static com.usw.sugo.global.exception.ExceptionType.PAYLOAD_NOT_VALID;

import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.note.notecontent.service.NoteContentService;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.refreshtoken.service.RefreshTokenService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import com.usw.sugo.domain.user.useremailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.user.userlikepost.service.UserLikePostService;
import com.usw.sugo.global.infrastructure.aws.ses.SendEmailServiceBySES;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.util.factory.BCryptPasswordFactory;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserServiceUtility userServiceUtility;
    private final RefreshTokenService refreshTokenService;
    private final SendEmailServiceBySES sendEmailServiceBySES;
    private final UserEmailAuthService userEmailAuthService;
    private final ProductPostService productPostService;
    private final UserLikePostService userLikePostService;
    private final NoteService noteService;
    private final NoteContentService noteContentService;

    public Map<String, Boolean> executeIsLoginIdExist(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            return getExistFlag();
        }
        return getNotExistFlag();
    }

    public Map<String, Boolean> executeIsEmailExist(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return getExistFlag();
        }
        return getNotExistFlag();
    }

    public Map<String, Boolean> executeFindLoginId(String email) {
        sendEmailServiceBySES.sendFindLoginIdResult(email,
            userServiceUtility.loadUserByEmail(email).getLoginId());
        return getSuccessFlag();
    }

    public Map<String, Boolean> executeFindPassword(String email, User user) {
        final String newPassword = userServiceUtility.initPassword(user);
        sendEmailServiceBySES.sendFindPasswordResult(email, newPassword);
        return getSuccessFlag();
    }

    @Transactional
    public Map<String, Object> executeJoin(
        String loginId, String email, String password, String department, Boolean pushAlarmStatus,
        String fcmToken
    ) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new CustomException(DUPLICATED_EMAIL);
        } else if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new CustomException(DUPLICATED_LOGINID);
        }
        userServiceUtility.validateSuwonUniversityEmailForm(email);
        final User requestUser = userServiceUtility.softJoin(
            loginId, email, password, department, pushAlarmStatus, fcmToken
        );
        final String authPayload = userEmailAuthService.saveUserEmailAuth(requestUser);
        sendEmailServiceBySES.sendStudentAuthContent(requestUser.getEmail(), authPayload);

        return new HashMap<>() {{
            put(SUCCESS.getResult(), true);
            put("id", requestUser.getId());
        }};
    }

    public Map<String, Boolean> executeAuthEmailPayload(String payload, Long userId) {
        final UserEmailAuth requestUserEmailAuth = userEmailAuthService.loadUserEmailAuthByUser(
            userServiceUtility.loadUserById(userId)
        );
        if (requestUserEmailAuth.getPayload().equals(payload)) {
            final User requestUser = requestUserEmailAuth.getUser();
            requestUserEmailAuth.confirmToken();
            requestUser.encryptPassword(requestUser.getPassword());
            requestUser.modifyingStatusToAvailable();
            userEmailAuthService.deleteConfirmedEmailAuthByUser(requestUser);
            return getSuccessFlag();
        }
        throw new CustomException(PAYLOAD_NOT_VALID);
    }

    public Map<String, Boolean> executeEditPassword(User user, String newPassword) {
        final User requestUser = userServiceUtility.loadUserById(user.getId());
        if (BCryptPasswordFactory.getBCryptPasswordEncoder()
            .matches(newPassword, requestUser.getPassword())
        ) {
            throw new CustomException(IS_SAME_PASSWORD);
        }
        requestUser.encryptPassword(newPassword);
        return getSuccessFlag();
    }

    public Map<String, Boolean> executeQuit(User user, String password) {
        if (userServiceUtility.matchingPassword(user.getId(), password)) {
            noteContentService.deleteNoteContentsByUser(user);
            noteService.deleteNotesByUser(user);
            userLikePostService.deleteLikePostsByUser(user);
            productPostService.deleteByUser(user);
            refreshTokenService.deleteByUser(user);
            userServiceUtility.deleteUser(user);
            return getSuccessFlag();
        }
        throw new CustomException(PASSWORD_NOT_CORRECT);
    }

    public UserPageResponseForm executeLoadUserPage(User user, Long userId) {
        // 요청유저와 userId가 같으면 마이페이지
        if (user.getId().equals(userId)) {
            return UserPageResponseForm.builder()
                .userId(userId)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .mannerGrade(user.getMannerGrade())
                .countMannerEvaluation(user.getCountMannerEvaluation())
                .countTradeAttempt(user.getCountTradeAttempt())
                .build();
        }
        final User otherUser = userServiceUtility.loadUserById(userId);
        return UserPageResponseForm.builder()
            .userId(userId)
            .email(otherUser.getEmail())
            .nickname(otherUser.getNickname())
            .mannerGrade(otherUser.getMannerGrade())
            .countMannerEvaluation(otherUser.getCountMannerEvaluation())
            .countTradeAttempt(otherUser.getCountTradeAttempt())
            .build();
    }

    public Map<String, Boolean> executeEvaluateManner(
        Long targetUserId, BigDecimal grade, User user
    ) {
        final User requestUser = userServiceUtility.loadUserById(user.getId());
        if (userServiceUtility.isBeforeDay(requestUser.getRecentEvaluationManner())) {
            userServiceUtility.loadUserById(targetUserId).updateMannerGrade(grade);
            requestUser.updateRecentEvaluationManner();
            return getSuccessFlag();
        }
        throw new CustomException(ALREADY_EVALUATION);
    }
}
