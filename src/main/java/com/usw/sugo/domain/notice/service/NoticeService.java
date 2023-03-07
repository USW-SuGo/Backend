package com.usw.sugo.domain.notice.service;

import static com.usw.sugo.global.valueobject.apiresult.ApiResultFactory.getSuccessFlag;
import static com.usw.sugo.global.exception.ExceptionType.POST_NOT_FOUND;

import com.usw.sugo.domain.notice.Notice;
import com.usw.sugo.domain.notice.repository.NoticeRepository;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.exception.ExceptionType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;


    public List<Notice> loadAllNotice(Pageable pageable) {
        return noticeRepository.loadAllNotice(pageable);
    }

    public Notice loadNoticeById(Long noticeId) {
        final Optional<Notice> notice = noticeRepository.findById(noticeId);
        if (noticeRepository.findById(noticeId).isEmpty()) {
            throw new CustomException(POST_NOT_FOUND);
        }
        return notice.get();
    }

    public Map<String, Boolean> save(User user, String title, String content) {
        validateUser(user);
        noticeRepository.save(Notice.builder()
            .title(title)
            .content(content)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build());
        return getSuccessFlag();
    }

    public Map<String, Boolean> edit(User user, Long noticeId, String title, String content) {
        validateUser(user);
        final Notice notice = validateNotice(noticeId);
        notice.updateTitle(title);
        notice.updateContent(content);
        notice.updateUpdatedAt();
        noticeRepository.save(notice);
        return getSuccessFlag();
    }

    public Map<String, Boolean> delete(User user, Long noticeId) {
        validateUser(user);
        validateNotice(noticeId);
        noticeRepository.deleteById(noticeId);
        return getSuccessFlag();
    }


    private void validateUser(User user) {
        if (!user.getStatus().equals("ADMIN")) {
            throw new CustomException(ExceptionType.NOT_ALLOWED);
        }
    }

    private Notice validateNotice(Long noticeId) {
        final Optional<Notice> notice = noticeRepository.findById(noticeId);
        if (notice.isEmpty()) {
            throw new CustomException(POST_NOT_FOUND);
        }
        return notice.get();
    }
}
