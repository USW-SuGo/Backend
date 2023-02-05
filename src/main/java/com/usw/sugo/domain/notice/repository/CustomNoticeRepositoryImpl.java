package com.usw.sugo.domain.notice.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.notice.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

import static com.usw.sugo.domain.notice.QNotice.notice;

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
}
