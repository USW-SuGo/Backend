package com.usw.sugo.domain.majoruser.emailauth.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import static com.usw.sugo.domain.majoruser.QUserEmailAuth.userEmailAuth;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomUserEmailAuthRepositoryImpl implements CustomUserEmailAuthRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public void confirmToken(String payload) {
        queryFactory
                .update(userEmailAuth)
                .set(userEmailAuth.status, true)
                .where(userEmailAuth.payload.eq(payload))
                .execute();
    }
}
