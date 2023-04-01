package com.usw.sugo.domain.notice.controller;

import com.usw.sugo.domain.notice.Notice;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticeDeleteRequest;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticePostRequest;
import com.usw.sugo.domain.notice.dto.NoticeRequestDto.NoticeUpdateRequest;
import com.usw.sugo.domain.notice.service.NoticeService;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.annotation.ApiLogger;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiLogger
@RequiredArgsConstructor
@RestController
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public List<Notice> loadAllNoticeByPaging(Pageable pageable) {
        return noticeService.loadAllNotice(pageable);
    }

    @GetMapping("/{noticeId}")
    public Notice loadNotice(@PathVariable @Valid Long noticeId) {
        return noticeService.loadNoticeById(noticeId);
    }

    @PostMapping
    public Map<String, Boolean> saveNotice(
        @RequestHeader String authorization,
        @RequestBody @Valid NoticePostRequest noticePostRequest,
        @AuthenticationPrincipal User user) {
        return noticeService.save(user, noticePostRequest.getTitle(),
            noticePostRequest.getContent());
    }

    @PutMapping
    public Map<String, Boolean> editNotice(
        @RequestHeader String authorization,
        @RequestBody @Valid NoticeUpdateRequest noticeUpdateRequest,
        @AuthenticationPrincipal User user) {
        return noticeService.edit(
            user,
            noticeUpdateRequest.getNoticeId(),
            noticeUpdateRequest.getTitle(),
            noticeUpdateRequest.getContent());
    }

    @DeleteMapping
    public Map<String, Boolean> deleteNotice(
        @RequestHeader String authorization,
        @RequestBody @Valid NoticeDeleteRequest noticeDeleteRequest,
        @AuthenticationPrincipal User user) {
        return noticeService.delete(user, noticeDeleteRequest.getNoticeId());
    }
}
