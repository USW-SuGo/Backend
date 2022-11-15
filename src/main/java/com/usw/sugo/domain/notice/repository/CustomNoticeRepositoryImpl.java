package com.usw.sugo.domain.notice.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.notice.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.usw.sugo.domain.notice.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomNoticeRepositoryImpl implements CustomNoticeRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notice> loadAllNotice(Pageable pageable) {
        return queryFactory
                .selectFrom(notice)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public void editNotice(long noticeId, String title, String content) {
        queryFactory
                .update(notice)
                .set(notice.title, title)
                .set(notice.content, content)
                .set(notice.updatedAt, LocalDateTime.now())
                .where(notice.id.eq(noticeId))
                .execute();
    }
}
