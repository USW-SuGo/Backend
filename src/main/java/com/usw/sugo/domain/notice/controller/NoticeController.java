package com.usw.sugo.domain.notice.controller;

import com.usw.sugo.domain.notice.Notice;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticeDeleteRequest;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticePostRequest;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticeUpdateRequest;
import com.usw.sugo.domain.notice.repository.NoticeRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ErrorCode;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notice")
public class NoticeController {


    private final NoticeRepository noticeRepository;
    private final JwtResolver jwtResolver;

    /**
     * 공지사항 모두 조회하기 (페이징)
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<Object> loadAllNoticeByPaging(Pageable pageable) {
        return ResponseEntity
                .ok()
                .body(noticeRepository.loadAllNotice(pageable));
    }

    /**
     * 특정 글의 공지사항 조회하기
     * @param noticeId
     * @return
     */
    @GetMapping("/")
    public ResponseEntity<Object> loadNotice(@RequestParam Long noticeId) {
        return ResponseEntity
                .ok()
                .body(noticeRepository.findById(noticeId).get());
    }

    /**
     * 공지사항 작성하기
     *
     * @param authorization
     * @return
     */
    @PostMapping
    public ResponseEntity<HashMap<String, Boolean>> writeNotice(@RequestHeader String authorization,
                                              @RequestBody NoticePostRequest noticePostRequest) {

        // 관리자 권한이 아니면 에러
        if (!jwtResolver.jwtResolveToUserStatus(authorization.substring(7)).equals("ADMIN")) {
            throw new CustomException(ErrorCode.USER_UNAUTHORIZED);
        }

        Notice newNotice = Notice.builder()
                .title(noticePostRequest.getTitle())
                .content(noticePostRequest.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        noticeRepository.save(newNotice);

        return ResponseEntity
                .ok()
                .body(new HashMap<>(){{put("Success", true);}});
    }

    /**
     * 공지사항 수정하기
     *
     * @param authorization
     * @return
     */
    @PutMapping
    public ResponseEntity<HashMap<String, Boolean>> updateNotice(@RequestHeader String authorization,
                                                                @RequestBody NoticeUpdateRequest noticeUpdateRequest) {

        // 관리자 권한이 아니면 에러
        if (!jwtResolver.jwtResolveToUserStatus(authorization.substring(7)).equals("ROLE_AVAILABLE")) {
            throw new CustomException(ErrorCode.USER_UNAUTHORIZED);
        }

        noticeRepository.editNotice(
                noticeUpdateRequest.getNoticeId(),
                noticeUpdateRequest.getTitle(),
                noticeUpdateRequest.getContent());

        return ResponseEntity
                .ok()
                .body(new HashMap<>(){{put("Success", true);}});
    }

    /**
     * 공지사항 삭제하기
     *
     * @param authorization
     * @return
     */
    @DeleteMapping
    public ResponseEntity<HashMap<String, Boolean>> deleteNotice(@RequestHeader String authorization,
                                              @RequestBody NoticeDeleteRequest noticeDeleteRequest) {

        // 관리자 권한이 아니면 에러
        if (!jwtResolver.jwtResolveToUserStatus(authorization.substring(7)).equals("ROLE_AVAILABLE")) {
            throw new CustomException(ErrorCode.USER_UNAUTHORIZED);
        }
        noticeRepository.deleteById(noticeDeleteRequest.getId());

        return ResponseEntity
                .ok()
                .body(new HashMap<>(){{put("Success", true);}});
    }
}
