package com.usw.sugo.domain.user.user.service;

import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.ClosePosting;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import com.usw.sugo.domain.user.useremailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.user.userlikepost.service.UserLikePostService;
import com.usw.sugo.global.aws.ses.SendEmailServiceBySES;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.util.factory.BCryptPasswordFactory;
import com.usw.sugo.global.util.nickname.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.usw.sugo.domain.ApiResult.EXIST;
import static com.usw.sugo.domain.ApiResult.SUCCESS;
import static com.usw.sugo.global.exception.ExceptionType.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserServiceUtility userServiceUtility;
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

    public Map<String, Boolean> executeIsLoginIdExist(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            return overlapFlag;
        }
        return unOverlapFlag;
    }

    public Map<String, Boolean> executeIsEmailExist(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return overlapFlag;
        }
        return unOverlapFlag;
    }

    public Map<String, Boolean> executeFindLoginId(String email) {
        sendEmailServiceBySES.sendFindLoginIdResult(email, userServiceUtility.loadUserByEmail(email).getLoginId());
        return successFlag;
    }

    public Map<String, Boolean> executeFindPassword(String email, User user) {
        String newPassword = userServiceUtility.initPassword(user);
        sendEmailServiceBySES.sendFindPasswordResult(email, newPassword);
        return successFlag;
    }

    public Map<String, Object> executeJoin(String loginId, String email, String password, String department) {
        userServiceUtility.validateSuwonUniversityEmailForm(email);
        User requestUser = userServiceUtility.softJoin(loginId, email, password);
        requestUser.updateNickname(NicknameGenerator.generateNickname(department));

        String authPayload = userEmailAuthService.saveUserEmailAuth(requestUser);
        sendEmailServiceBySES.sendStudentAuthContent(requestUser.getEmail(), authPayload);
        return new HashMap<>() {{
            put(SUCCESS.getResult(), true);
            put("id", requestUser.getId());
        }};
    }

    public Map<String, Boolean> executeAuthEmailPayload(String payload, Long userId) {
        UserEmailAuth requestUserEmailAuth = userEmailAuthService.loadUserEmailAuthByUser(
                userServiceUtility.loadUserById(userId));
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

    public Map<String, Boolean> executeEditPassword(User user, String newPassword) {
        User requestUser = userServiceUtility.loadUserById(user.getId());
        if (BCryptPasswordFactory.getBCryptPasswordEncoder().matches(newPassword, requestUser.getPassword())) {
            throw new CustomException(IS_SAME_PASSWORD);
        }
        requestUser.encryptPassword(newPassword);
        return successFlag;
    }

    public Map<String, Boolean> executeQuit(User user, String password) {
        if (userServiceUtility.matchingPassword(user.getId(), password)) {
            noteService.deleteNoteByUser(user);
            productPostService.deleteByUser(user);
            userServiceUtility.deleteUser(user);
            return successFlag;
        }
        throw new CustomException(PASSWORD_NOT_CORRECT);
    }

    public UserPageResponseForm executeLoadUserPage(User user, Long userId, Pageable pageable) {
        // 요청유저와 userId가 같으면 마이페이지
        if (user.getId().equals(userId)) {
            return UserPageResponseForm.builder()
                    .userId(userId)
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .mannerGrade(user.getMannerGrade())
                    .countMannerEvaluation(user.getCountMannerEvaluation())
                    .countTradeAttempt(user.getCountTradeAttempt())
                    .myPostings(productPostService.myPostings(user, pageable))
                    .likePostings(userLikePostService.loadLikePosts(user.getId()))
                    .closePostings(executeLoadCloseMyPost(user, pageable))
                    .build();
        }
        User otherUser = userServiceUtility.loadUserById(userId);
        return UserPageResponseForm.builder()
                .userId(userId)
                .email(otherUser.getEmail())
                .nickname(otherUser.getNickname())
                .mannerGrade(otherUser.getMannerGrade())
                .countMannerEvaluation(otherUser.getCountMannerEvaluation())
                .countTradeAttempt(otherUser.getCountTradeAttempt())
                .myPostings(productPostService.myPostings(otherUser, pageable))
                .closePostings(executeLoadCloseMyPost(otherUser, pageable))
                .build();
    }

    public Map<String, Boolean> executeEvaluateManner(Long targetUserId, BigDecimal grade, User user) {
        if (userServiceUtility.isBeforeDay(user.getRecentEvaluationManner())) {
            userServiceUtility.loadUserById(targetUserId).updateMannerGrade(grade);
            user.updateRecentEvaluationManner();
            return successFlag;
        }
        throw new CustomException(ALREADY_EVALUATION);
    }

    public List<ClosePosting> executeLoadCloseMyPost(User user, Pageable pageable) {
        return productPostService.loadClosePosting(user, pageable);
    }
}
