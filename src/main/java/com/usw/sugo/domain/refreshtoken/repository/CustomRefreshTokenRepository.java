package com.usw.sugo.domain.refreshtoken.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomRefreshTokenRepository {
    void refreshPayload(Long userId, String payload);

    void deleteRefreshTokenInformation(Long userId);
}
