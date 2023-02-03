package com.usw.sugo.domain.user.useremailauth.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.user.useremailauth.UserEmailAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static com.usw.sugo.domain.user.useremailauth.QUserEmailAuth.userEmailAuth;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomUserEmailAuthRepositoryImpl implements CustomUserEmailAuthRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByUserId(Long userId) {
        queryFactory
                .delete(userEmailAuth)
                .where(userEmailAuth.id.eq(userId))
                .execute();
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
