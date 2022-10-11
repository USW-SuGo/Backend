package com.usw.sugo.domain.majoruser.user.controller;

import com.usw.sugo.domain.majorproduct.repository.productpost.ProductPostRepository;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.UserEmailAuth;
import com.usw.sugo.domain.majoruser.emailauth.repository.UserEmailAuthRepository;
import com.usw.sugo.domain.majoruser.emailauth.service.UserEmailAuthService;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.*;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.IsEmailExistResponse;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.IsLoginIdExistResponse;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.MyPageResponse;
import com.usw.sugo.domain.majoruser.user.dto.UserResponseDto.OtherUserPageResponse;
import com.usw.sugo.domain.majoruser.user.repository.UserRepository;
import com.usw.sugo.domain.majoruser.user.service.UserService;
import com.usw.sugo.domain.majoruser.userlikepost.repository.UserLikePostRepository;
import com.usw.sugo.global.aws.ses.AuthSuccessViewForm;
import com.usw.sugo.global.aws.ses.SendEmailServiceFromSES;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtResolver;
import com.usw.sugo.global.util.nickname.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static com.usw.sugo.global.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final JwtResolver jwtResolver;
    private final UserService userService;
    private final NicknameGenerator nicknameGenerator;
    private final UserRepository userRepository;
    private final UserLikePostRepository userLikePostRepository;
    private final ProductPostRepository productPostRepository;

    private final SendEmailServiceFromSES sendEmailServiceFromSES;

    private final AuthSuccessViewForm authSuccessViewForm;

    private final UserEmailAuthRepository userEmailAuthRepository;

    private final UserEmailAuthService userEmailAuthService;


    @PostMapping("/check-loginId")
    public ResponseEntity<IsLoginIdExistResponse> checkLoginId(@Valid @RequestBody IsLoginIdExistRequest isLoginIdExistRequest) {

        IsLoginIdExistResponse isLoginIdExistResponse = new IsLoginIdExistResponse(false);

        // 아이디가 중복되었을 때 에러
        if (userRepository.findByLoginId(isLoginIdExistRequest.getLoginId()).isPresent()) {
            throw new CustomException(DUPLICATED_LOGINID);
        }
        return ResponseEntity.status(OK).body(isLoginIdExistResponse);
    }

    // 이메일 중복 확인
    @PostMapping("/check-email")
    public ResponseEntity<IsEmailExistResponse> checkEmail(@Valid @RequestBody IsEmailExistRequest isEmailExistRequest) {

        IsEmailExistResponse isEmailExistResponse = new IsEmailExistResponse(false);

        // 이메일이 DB에 존재하면
        if (userRepository.findByEmail(isEmailExistRequest.getEmail()).isPresent()) {
            isEmailExistResponse.setExist(true);
        }

        return ResponseEntity.status(OK).body(isEmailExistResponse);
    }

    /**
     * 아이디 찾기 결과는 이메일로 전송
     *
     * @param findLoginIdRequest
     * @return
     */
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, Boolean>> findId(@Valid @RequestBody FindLoginIdRequest findLoginIdRequest) {

        User requestUser = userRepository.findByEmail(findLoginIdRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        sendEmailServiceFromSES.sendFindLoginIdResult(findLoginIdRequest.getEmail(), requestUser.getLoginId());

        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
        }});
    }

    /**
     * 비밀번호 초기화 메일 전송
     * @param authorization
     * @param sendPasswordRequest
     * @return
     */
    @PostMapping("/find-pw")
    public ResponseEntity<Map<String, Boolean>> sendPasswordEmail(@RequestHeader String authorization,
                                                                  @Valid @RequestBody SendPasswordRequest sendPasswordRequest) {

        String newPassword = userService.initPassword(
                jwtResolver.jwtResolveToUserId(authorization.substring(7)));

        sendEmailServiceFromSES.sendFindPasswordResult(sendPasswordRequest.getEmail(),newPassword);

        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
        }});
    }

    //이메일 인증 링크 클릭 시
    @GetMapping("/verify-authorization-email")
    public String ConfirmEmail(@RequestParam("auth") String payload) {

        // 이메일 인증을 요청한 사용자가 DB 에 없으면 에러
        UserEmailAuth requestUserEmail = userEmailAuthRepository.findByPayload(payload)
                .orElseThrow(() -> new CustomException(INVALID_AUTH_TOKEN));

        User requestUser = requestUserEmail.getUser();

        // 이메일 인증 로직 수행
        userEmailAuthService.authorizeToken(payload);

        // 비밀번호 암호화
        userRepository.passwordEncode(requestUser, requestUser.getId());

        // 유저 Status 컬럼 수정 -> Available
        userRepository.modifyingStatusToAvailable(requestUser.getId());

        return authSuccessViewForm.successParagraph();
    }

    /**
     * 회원가입 컨트롤러
     *
     * @param detailJoinRequest
     * @return
     */
    @PostMapping("/join")
    public ResponseEntity<HashMap<String, Boolean>> detailJoin(@Valid @RequestBody DetailJoinRequest detailJoinRequest) {

        if (userRepository.findByLoginId(detailJoinRequest.getLoginId()).isPresent()) {
            throw new CustomException(DUPLICATED_LOGINID);
        }
        if (userRepository.findByEmail(detailJoinRequest.getEmail()).isPresent()) {
            throw new CustomException(DUPLICATED_EMAIL);
        }

        User requestUser = userService.softJoin(detailJoinRequest);

        // 닉네임 자동발급 수행 및 유저 변경시각 타임스탬프
        userRepository.editNickname(
                requestUser.getId(),
                nicknameGenerator.generateNickname(detailJoinRequest.getDepartment()));

        System.out.println(requestUser.getId());

        String authPayload = "http://localhost:8080/user/verify-authorization-email?auth=" +
                userEmailAuthService.createEmailAuthToken(requestUser.getId());

//        String authPayload = "https://api.sugo-diger.com/user/verify-authorization-email?auth=" +
//                userEmailAuthService.createEmailAuthToken(requestUser.getId());

        // 이메일 발송
        sendEmailServiceFromSES.sendStudentAuthContent(requestUser.getEmail(), authPayload);

        // 반환
        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
        }});
    }

    // 비밀번호 수정
    @PutMapping("/password")
    public ResponseEntity<HashMap<String, Boolean>> editPassword(
            @RequestHeader String authorization,
            @Valid @RequestBody EditPasswordRequest editPasswordRequest) {

        // 이전 비밀번호와 같은 내용으로 변경하려 할 때
        if (userService.matchingPassword(editPasswordRequest.getId(), editPasswordRequest.getPassword())) {
            throw new CustomException(IS_SAME_PASSWORD);
        }

        // 비밀번호 수정
        userRepository.editPassword(editPasswordRequest.getId(), editPasswordRequest.getPassword());
        // 유저 변경 시각 타임스탬프
        userRepository.setModifiedDate(editPasswordRequest.getId());
        //반환

        return ResponseEntity.status(OK).body(new HashMap<>() {{
            put("Success", true);
        }});
    }

    // 회원탈퇴
    @DeleteMapping
    public ResponseEntity<HashMap<String, Boolean>> deleteUser(@RequestHeader String authorization,
                                                               @Valid @RequestBody QuitRequest quitRequest) {

        Long requestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        // DB에 없는 유저면 에러
        userRepository.findByEmail(quitRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        // 비밀번호가 일치하지 않을 때
        if (!userService.matchingPassword(requestUserId, quitRequest.getPassword()))
            throw new CustomException(PASSWORD_NOT_CORRECT);


        // 비밀번호 수정
        userRepository.deleteById(requestUserId);

        //반환
        return ResponseEntity
                .status(OK)
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    // 마이 페이지
    @GetMapping
    public ResponseEntity<MyPageResponse> userPage(@RequestHeader String authorization, Pageable pageable) {

        long targetUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        User requestUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        MyPageResponse myPageResponse = MyPageResponse.builder()
                .userId(targetUserId)
                .email(requestUser.getEmail())
                .nickname(requestUser.getNickname())
                .mannerGrade(requestUser.getMannerGrade())
                .countMannerEvaluation(requestUser.getCountMannerEvaluation())
                .countTradeAttempt(requestUser.getCountTradeAttempt())
                .myPosting(productPostRepository.loadUserPageList(requestUser, pageable))
                .likePosting(userLikePostRepository.loadMyLikePosting(requestUser.getId()))
                .build();
        return ResponseEntity.status(OK).body(myPageResponse);
    }

    // 다른 유저의 페이지
    @GetMapping("/")
    public ResponseEntity<OtherUserPageResponse> otherUserPage(@RequestHeader String authorization,
                                                               @RequestParam long userId, Pageable pageable) {

        userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        User targetUser = userRepository.findById(userId).get();
        OtherUserPageResponse otherUserPageResponse = OtherUserPageResponse.builder()
                .userId(userId)
                .nickname(targetUser.getNickname())
                .email(targetUser.getEmail())
                .mannerGrade(targetUser.getMannerGrade())
                .countMannerEvaluation(targetUser.getCountMannerEvaluation())
                .countTradeAttempt(targetUser.getCountTradeAttempt())
                .myPosting(productPostRepository.loadUserPageList(targetUser, pageable))
                .build();
        return ResponseEntity.status(OK).body(otherUserPageResponse);
    }


    /**
     * 상대 유저 매너 평가하기
     *
     * @param authorization
     * @param mannerEvaluationRequest
     * @return
     */

    @PostMapping("/manner")
    public ResponseEntity<?> userPage(@RequestHeader String authorization,
                                      @Valid @RequestBody MannerEvaluationRequest mannerEvaluationRequest) {
        long requestUserId = jwtResolver.jwtResolveToUserId(authorization.substring(7));

        // 평가를 요청한 유저가 존재하지 않으면 에러
        userRepository.findById(requestUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));
        // 평가 대상 유저가 존재하지 않으면 에러
        userRepository.findById(mannerEvaluationRequest.getTargetUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_EXIST));

        User requestUser = userRepository.findById(requestUserId).get();

        // 매너 평가한지 하루가 지나지 않았을 때
        if (userService.isBeforeDay(requestUser.getRecentEvaluationManner())) {
            userRepository.setRecentMannerGradeDate(
                    mannerEvaluationRequest.getGrade(), mannerEvaluationRequest.getTargetUserId(), requestUserId);
        } else {
            throw new CustomException(ALREADY_EVALUATION);
        }

        return ResponseEntity.status(OK).body(new HashMap<String, Boolean>() {{
            put("Success", true);
        }});
    }
}
