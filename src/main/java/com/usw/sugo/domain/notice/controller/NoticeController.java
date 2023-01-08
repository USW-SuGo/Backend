package com.usw.sugo.domain.notice.controller;

import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticeDeleteRequest;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticePostRequest;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticeUpdateRequest;
import com.usw.sugo.domain.notice.entity.Notice;
import com.usw.sugo.domain.notice.repository.NoticeRepository;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import com.usw.sugo.global.jwt.JwtResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.usw.sugo.global.exception.ExceptionType.POST_NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notice")

public class NoticeController {
    private final NoticeRepository noticeRepository;
    private final JwtResolver jwtResolver;

    @GetMapping
    public ResponseEntity<Object> loadAllNoticeByPaging(Pageable pageable) {
        return ResponseEntity
                .ok()
                .body(noticeRepository.loadAllNotice(pageable));
    }

    @GetMapping("/")
    public ResponseEntity<Object> loadNotice(@RequestParam Long noticeId) {
        return ResponseEntity
                .ok()
                .body(noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(POST_NOT_FOUND)));
    }

    @PostMapping
    public ResponseEntity<HashMap<String, Boolean>> writeNotice(
            @RequestHeader String authorization, @RequestBody NoticePostRequest noticePostRequest) {
        if (!jwtResolver.jwtResolveToUserStatus(authorization.substring(7)).equals("ADMIN")) {
            throw new CustomException(ExceptionType.USER_UNAUTHORIZED);
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
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    @PutMapping
    public ResponseEntity<HashMap<String, Boolean>> updateNotice(
            @RequestHeader String authorization, @RequestBody NoticeUpdateRequest noticeUpdateRequest) {
        if (!jwtResolver.jwtResolveToUserStatus(authorization.substring(7)).equals("ADMIN")) {
            throw new CustomException(ExceptionType.USER_UNAUTHORIZED);
        }

        noticeRepository.editNotice(
                noticeUpdateRequest.getNoticeId(),
                noticeUpdateRequest.getTitle(),
                noticeUpdateRequest.getContent());

        return ResponseEntity
                .ok()
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> deleteNotice(
            @RequestHeader String authorization, @RequestBody NoticeDeleteRequest noticeDeleteRequest) {

        if (!jwtResolver.jwtResolveToUserStatus(authorization.substring(7)).equals("ADMIN")) {
            throw new CustomException(ExceptionType.USER_UNAUTHORIZED);
        }

        noticeRepository.deleteById(noticeDeleteRequest.getId());

        return ResponseEntity
                .ok()
                .body(new HashMap<>() {{
                    put("Success", true);
                }});
    }
}
