package com.usw.sugo.domain.majorproduct.repository.productpostfile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomProductPostFileRepositoryImpl implements CustomProductPostFileRepository {

    private final JPAQueryFactory queryFactory;
}
