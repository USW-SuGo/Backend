package com.usw.sugo.domain.note.notefile.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomNoteFileRepositoryImpl implements CustomNoteFileRepository {

    private final JPAQueryFactory queryFactory;
}
