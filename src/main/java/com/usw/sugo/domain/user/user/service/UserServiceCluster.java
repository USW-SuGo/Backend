package com.usw.sugo.domain.user.user.service;

import com.usw.sugo.domain.note.note.service.NoteService;
import com.usw.sugo.domain.productpost.productpost.service.ProductPostService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.domain.user.user.dto.UserRequestDto;
import com.usw.sugo.domain.user.user.dto.UserRequestDto.AuthEmailPayloadForm;
import com.usw.sugo.domain.user.user.dto.UserResponseDto;
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

import java.util.HashMap;
import java.util.Map;

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
        put("Exist", true);
    }};
    private static final Map<String, Boolean> unOverlapFlag = new HashMap<>() {{
        put("Exist", false);
    }};
    private static final Map<String, Boolean> successFlag = new HashMap<>() {{
        put("Success", true);
    }};
    private static final Map<String, Boolean> failFlag = new HashMap<>() {{
        put("Success", false);
    }};


    // Service에서 DTO 의존 시 쌍방향 의존성이 생겨버림.
    public Map<String, Boolean> executeIsLoginIdExist(UserRequestDto.IsLoginIdExistRequestForm isLoginIdExistRequestForm) {
        if (userService.validateLoginIdDuplicated(isLoginIdExistRequestForm.getLoginId())) {
            return overlapFlag;
        }
        return unOverlapFlag;
    }

    public Map<String, Boolean> executeIsEmailExist(UserRequestDto.IsEmailExistRequestForm isEmailExistRequestForm) {
        userService.validateSuwonUniversityEmailForm(isEmailExistRequestForm.getEmail());
        if (userService.validateEmailDuplicated(isEmailExistRequestForm.getEmail())) {
            return overlapFlag;
        }
        return unOverlapFlag;
    }

    public Map<String, Boolean> executeFindLoginId(UserRequestDto.FindLoginIdRequestForm findLoginIdRequestForm, User user) {
        sendEmailServiceBySES.sendFindLoginIdResult(findLoginIdRequestForm.getEmail(), user.getLoginId());
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> executeFindPassword(UserRequestDto.FindPasswordRequestForm findPasswordRequestForm, User user) {
        String newPassword = userService.initPassword(user);
        sendEmailServiceBySES.sendFindPasswordResult(findPasswordRequestForm.getEmail(), newPassword);
        return successFlag;
    }

    @Transactional
    public Map<String, Object> executeJoin(UserRequestDto.DetailJoinRequestForm detailJoinRequestForm) {
        userService.validateLoginIdDuplicated(detailJoinRequestForm.getLoginId());
        userService.validateSuwonUniversityEmailForm(detailJoinRequestForm.getEmail());
        userService.validateEmailDuplicated(detailJoinRequestForm.getEmail());
        User requestUser = userService.softJoin(detailJoinRequestForm);
        requestUser.updateNickname(NicknameGenerator.generateNickname(detailJoinRequestForm.getDepartment()));

        String authPayload = userEmailAuthService.saveUserEmailAuth(requestUser);
        sendEmailServiceBySES.sendStudentAuthContent(requestUser.getEmail(), authPayload);
        return new HashMap<>() {{
            put("Success", true);
            put("id", requestUser.getId());
        }};
    }

    @Transactional
    public Map<String, Boolean> executeAuthEmailPayload(AuthEmailPayloadForm authEmailPayloadForm) {
        String payload = authEmailPayloadForm.getPayload();
        UserEmailAuth requestUserEmailAuth = userEmailAuthService.loadUserEmailAuthByUser(
                userService.loadUserById(authEmailPayloadForm.getUserId()));

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
    public Map<String, Boolean> executeEditPassword(UserRequestDto.EditPasswordRequestForm editPasswordRequestForm, User user) {
        user.encryptPassword(editPasswordRequestForm.getPassword());
        user.encryptPassword(editPasswordRequestForm.getPassword());
        return successFlag;
    }

    @Transactional
    public Map<String, Boolean> executeQuit(UserRequestDto.QuitRequestForm quitRequestForm, User user) {
        noteService.deleteNoteByUser(user);
        productPostService.deleteByUser(user);
        userService.deleteUser(user);
        return successFlag;
    }

    public UserResponseDto.UserPageResponseForm executeLoadUserPage(User user, Long userId, Pageable pageable) {
        if (user.getId().equals(userId)) {
            return UserResponseDto.UserPageResponseForm.builder()
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
        return UserResponseDto.UserPageResponseForm.builder()
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
    public Map<String, Boolean> executeEvaluateManner(UserRequestDto.MannerEvaluationRequestForm mannerEvaluationRequestForm, User user) {
        if (userService.isBeforeDay(user.getRecentEvaluationManner())) {
//            userRepository.setRecentMannerGradeDate(
//                    mannerEvaluationRequestForm.getGrade(), mannerEvaluationRequestForm.getTargetUserId(), user.getId());
        } else {
            throw new CustomException(ALREADY_EVALUATION);
        }
        return successFlag;
    }
}
