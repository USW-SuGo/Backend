package com.usw.sugo.domain.user.service;

import com.usw.sugo.domain.emailauth.UserEmailAuth;
import com.usw.sugo.domain.emailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.emailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.notefile.service.NoteFileService;
import com.usw.sugo.domain.productpost.repository.ProductPostRepository;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.dto.UserRequestDto.*;
import com.usw.sugo.domain.user.dto.UserResponseDto.UserPageResponseForm;
import com.usw.sugo.domain.user.repository.UserRepository;
import com.usw.sugo.domain.userlikepost.repository.UserLikePostRepository;
import com.usw.sugo.global.aws.ses.SendEmailServiceBySES;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.util.nickname.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.usw.sugo.global.exception.ExceptionType.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceCluster {
    private final UserService userService;
    private final SendEmailServiceBySES sendEmailServiceBySES;
    private final NicknameGenerator nicknameGenerator;
    private final UserEmailAuthService userEmailAuthService;
    private final UserEmailAuthRepository userEmailAuthRepository;
    private final UserRepository userRepository;
    private final NoteFileService noteFileService;
    private final ProductPostRepository productPostRepository;
    private final UserLikePostRepository userLikePostRepository;

    private static final Map<String, Boolean> overlapFlag = new HashMap<>() {{
        put("Exist", true);
    }};
    private static final Map<String, Boolean> unOverlapFlag = new HashMap<>() {{
        put("Exist", true);
    }};
    private static final Map<String, Boolean> successFlag = new HashMap<>() {{
        put("Success", true);
    }};
    private static final Map<String, Boolean> failFlag = new HashMap<>() {{
        put("Success", false);
    }};

    public Map<String, Boolean> executeIsLoginIdExist(IsLoginIdExistRequestForm isLoginIdExistRequestForm) {
        if (userService.validateLoginIdDuplicated(isLoginIdExistRequestForm.getLoginId())) {
            return overlapFlag;
        }
        return unOverlapFlag;
    }

    public Map<String, Boolean> executeIsEmailExist(IsEmailExistRequestForm isEmailExistRequestForm) {
        userService.validateSuwonAcKrEmail(isEmailExistRequestForm.getEmail());
        if (userService.validateEmailDuplicated(isEmailExistRequestForm.getEmail())) {
            return overlapFlag;
        }
        return unOverlapFlag;
    }

    public Map<String, Boolean> executeFindLoginId(FindLoginIdRequestForm findLoginIdRequestForm, User user) {
        sendEmailServiceBySES.sendFindLoginIdResult(findLoginIdRequestForm.getEmail(), user.getLoginId());
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> executeFindPassword(FindPasswordRequestForm findPasswordRequestForm, User user) {
        String newPassword = userService.initPassword(user);
        sendEmailServiceBySES.sendFindPasswordResult(findPasswordRequestForm.getEmail(), newPassword);
        return successFlag;
    }

    @Transactional
    public Map<String, Object> executeJoin(DetailJoinRequestForm detailJoinRequestForm) {
        userService.validateLoginIdDuplicated(detailJoinRequestForm.getLoginId());
        userService.validateSuwonAcKrEmail(detailJoinRequestForm.getEmail());
        userService.validateEmailDuplicated(detailJoinRequestForm.getEmail());
        User requestUser = userService.softJoin(detailJoinRequestForm);
        requestUser.updateNickname(nicknameGenerator.generateNickname(detailJoinRequestForm.getDepartment()));
        String authPayload = userEmailAuthService.createEmailAuthPayload(requestUser.getId());
        sendEmailServiceBySES.sendStudentAuthContent(requestUser.getEmail(), authPayload);
        return new HashMap<>() {{
            put("Success", true);
            put("id", requestUser.getId());
        }};
    }

    @Transactional
    public Map<String, Boolean> executeAuthEmailPayload(AuthEmailPayloadForm authEmailPayloadForm) {
        String payload = authEmailPayloadForm.getPayload();
        UserEmailAuth requestUserEmailAuth = userEmailAuthRepository.findByUserId(authEmailPayloadForm.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_SEND_AUTH_EMAIL));

        if (requestUserEmailAuth.getPayload().equals(payload)) {
            User requestUser = requestUserEmailAuth.getUser();
            userEmailAuthService.authorizeEmailByPayload(payload);
            requestUser.encryptPassword(requestUser.getPassword());
            requestUser.modifyingStatusToAvailable();
            userEmailAuthRepository.deleteByUser(requestUser);
            return successFlag;
        }
        throw new CustomException(PAYLOAD_NOT_VALID);
    }

    @Transactional
    public Map<String, Boolean> executeEditPassword(EditPasswordRequestForm editPasswordRequestForm, User user) {
        user.encryptPassword(editPasswordRequestForm.getPassword());
        user.encryptPassword(editPasswordRequestForm.getPassword());
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> executeQuit(QuitRequestForm quitRequestForm, User user) {
        userService.isUserExistByLoginId(quitRequestForm.getLoginId());
        noteFileService.deleteNoteFile(user);
        userRepository.deleteById(user.getId());
        return successFlag;
    }

    public UserPageResponseForm executeLoadUserPage(User user, Long userId, Pageable pageable) {
        UserPageResponseForm userPageResponseForm = UserPageResponseForm.builder()
                .userId(userId)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .mannerGrade(user.getMannerGrade())
                .countMannerEvaluation(user.getCountMannerEvaluation())
                .countTradeAttempt(user.getCountTradeAttempt())
                .build();
        if (user.getId().equals(userId)) {
            return UserPageResponseForm.builder()
                    .myPosting(productPostRepository.loadUserWritingPostingList(user, pageable))
                    .likePosting(userLikePostRepository.loadMyLikePosting(user.getId()))
                    .build();
        } else {
            return UserPageResponseForm.builder()
                    .myPosting(productPostRepository.loadUserWritingPostingList(user, pageable))
                    .build();
        }
    }

    @Transactional
    public Map<String, Boolean> executeEvaluateManner(MannerEvaluationRequestForm mannerEvaluationRequestForm, User user) {
        if (userService.isBeforeDay(user.getRecentEvaluationManner())) {
            userRepository.setRecentMannerGradeDate(
                    mannerEvaluationRequestForm.getGrade(), mannerEvaluationRequestForm.getTargetUserId(), user.getId());
        } else {
            throw new CustomException(ALREADY_EVALUATION);
        }
        return successFlag;
    }
}
