package com.usw.sugo.domain.notefile.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class CustomNoteFileRepositoryImpl implements CustomNoteFileRepository {

    private final JPAQueryFactory queryFactory;
}
