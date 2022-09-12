package com.usw.sugo.domain.majoruser.useremailauth.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majoruser.QUserEmailAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static com.usw.sugo.domain.majoruser.QUserEmailAuth.*;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomUserEmailAuthRepositoryImpl implements CustomUserEmailAuthRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public void confirmToken(String payload) {
        queryFactory
                .update(userEmailAuth)
                .set(userEmailAuth.expiredAt, LocalDateTime.now())
                .set(userEmailAuth.status, "Confirmed")
                .where(userEmailAuth.payload.eq(payload))
                .execute();
    }
}
