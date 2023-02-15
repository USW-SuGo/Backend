package com.usw.sugo.domain.refreshtoken.service;

import static com.usw.sugo.global.exception.ExceptionType.JWT_MALFORMED_EXCEPTION;

import com.usw.sugo.domain.refreshtoken.RefreshToken;
import com.usw.sugo.domain.refreshtoken.repository.RefreshTokenRepository;
import com.usw.sugo.domain.user.user.User;
import com.usw.sugo.global.exception.CustomException;
import com.usw.sugo.global.jwt.JwtGenerator;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtGenerator jwtGenerator;

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public RefreshToken loadRefreshTokenByPayload(String requestRefreshToken) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByPayload(
            requestRefreshToken);
        if (refreshToken.isPresent()) {
            return refreshToken.get();
        }
        throw new CustomException(JWT_MALFORMED_EXCEPTION);
    }

    public Map<String, String> reIssueToken(RefreshToken refreshToken) {
        User requestUser = refreshToken.getUser();
        String accessTokenPayload = jwtGenerator.generateAccessToken(requestUser);
        String refreshTokenPayload = jwtGenerator.updateRefreshToken(requestUser);

        Map<String, String> result = jwtGenerator.wrapTokenPair(accessTokenPayload,
            refreshTokenPayload);
        return result;
    }
}
