package com.usw.sugo.domain.majoruser.emailauth.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static com.usw.sugo.domain.majoruser.QUser.user;
import static com.usw.sugo.domain.majoruser.QUserEmailAuth.userEmailAuth;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomUserEmailAuthRepositoryImpl implements CustomUserEmailAuthRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void confirmToken(String payload) {
        queryFactory
                .update(userEmailAuth)
                .set(userEmailAuth.status, true)
                .where(userEmailAuth.payload.eq(payload))
                .execute();
    }

    @Override
    public void deleteBeforeWeek() {
        queryFactory
                .delete(userEmailAuth)
                .where(userEmailAuth.user.id.eq(
                        JPAExpressions
                                .select(userEmailAuth.user.id)
                                .from(userEmailAuth)
                                .where(userEmailAuth.createdAt
                                        .before(LocalDateTime.now().minusSeconds(1)))))
                .execute();

        queryFactory
                .delete(user)
                .where(user.id.eq(
                        JPAExpressions
                                .select(userEmailAuth.user.id)
                                .from(userEmailAuth)
                                .where(userEmailAuth.createdAt
                                        .before(LocalDateTime.now().minusSeconds(1)))))
                .execute();

    }
}
