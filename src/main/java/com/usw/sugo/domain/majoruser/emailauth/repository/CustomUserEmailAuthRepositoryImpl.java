package com.usw.sugo.domain.majoruser.emailauth.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majoruser.UserEmailAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

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
    public void deleteBeforeWeek(List<UserEmailAuth> notAuthenticatedUserEmailAuth) {

        for (UserEmailAuth notAuth : notAuthenticatedUserEmailAuth) {
            queryFactory
                    .delete(userEmailAuth)
                    .where(userEmailAuth.id.eq(notAuth.getId()))
                    .execute();

            queryFactory
                    .delete(user)
                    .where(user.id.eq(notAuth.getUser().getId()))
                    .execute();
        }
    }

    @Override
    public List<UserEmailAuth> loadNotAuthenticatedUserEmailAuth() {
        return queryFactory
                .selectFrom(userEmailAuth)
                .where(userEmailAuth.createdAt
                        .before(LocalDateTime.now().minusMinutes(10)))
                .fetch();
    }
}
