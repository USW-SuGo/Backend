package com.usw.sugo.domain.refreshtoken.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.refreshtoken.QRefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static com.usw.sugo.domain.refreshtoken.QRefreshToken.refreshToken;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomRefreshTokenRepositoryImpl implements CustomRefreshTokenRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void refreshPayload(Long userId, String payload) {
        queryFactory
                .update(refreshToken)
                .set(refreshToken.payload, payload)
                .set(refreshToken.updatedAt, LocalDateTime.now())
                .where(refreshToken.userId.eq(userId))
                .execute();
    }

    @Override
    public void deleteRefreshTokenInformation(Long userId) {
        queryFactory
                .delete(refreshToken)
                .where(refreshToken.userId.eq(userId))
                .execute();
    }
}
