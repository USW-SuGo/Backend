package com.usw.sugo.domain.user.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.user.entity.QUser;
import com.usw.sugo.domain.user.entity.User;
import com.usw.sugo.domain.user.user.dto.QUserResponseDto_UserPageResponse;
import com.usw.sugo.domain.user.user.dto.UserResponseDto.UserPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.usw.sugo.domain.user.entity.QUser.user;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;
    private final BCryptPasswordEncoder encoder;

    @Override
    public UserPageResponse loadUserPage(User requestUser) {
        UserPageResponse userPageResponse = queryFactory
                .select(new QUserResponseDto_UserPageResponse(
                        user.id.as("userId"), user.email, user.nickname, user.mannerGrade,
                        user.countMannerEvaluation, user.countTradeAttempt))
                .from(user)
                .where(user.id.eq(requestUser.getId()))
                .fetchOne();
        return userPageResponse;
    }

    @Override
    public void modifyingStatusToAvailable(Long id) {
        queryFactory
                .update(user)
                .set(user.status, "AVAILABLE")
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void passwordEncode(User requestUser, Long userId) {
        queryFactory
                .update(user)
                .set(user.password, encoder.encode(requestUser.getPassword()))
                .where(user.email.eq(requestUser.getEmail()))
                .execute();
    }

    @Override
    public void editPassword(Long id, String password) {
        queryFactory
                .update(user)
                .set(user.password, encoder.encode(password))
                .where(user.id.eq(id))
                .execute();
    }

    // ???????????? ??????*
    @Override
    public void setModifiedDate(Long id) {
        queryFactory
                .update(user)
                .set(user.updatedAt, LocalDateTime.now())
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void setRecentMannerGradeDate(BigDecimal grade, long targetUserId, long evaluatingUserId) {
        queryFactory
                .update(user)
                .set(user.mannerGrade, (user.mannerGrade.add(grade)).divide(user.countMannerEvaluation))
                .set(user.countMannerEvaluation, user.countMannerEvaluation.add(1))
                .where(user.id.eq(targetUserId))
                .execute();

        queryFactory
                .update(user)
                .set(user.recentEvaluationManner, LocalDateTime.now())
                .where(user.id.eq(evaluatingUserId))
                .execute();
    }

    @Override
    public void setRecentUpPostingDate(Long id) {
        queryFactory
                .update(user)
                .set(user.recentUpPost, LocalDateTime.now())
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void editNickname(long id, String nickname) {
        queryFactory
                .update(user)
                .set(user.nickname, nickname)
                .set(user.updatedAt, LocalDateTime.now())
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void plusCountTradeAttempt(long sellerId, long buyerId) {
        queryFactory
                .update(user)
                .set(user.countTradeAttempt, user.countTradeAttempt.add(1))
                .where(user.id.eq(sellerId))
                .execute();
        queryFactory
                .update(user)
                .set(user.countTradeAttempt, user.countTradeAttempt.add(1))
                .where(user.id.eq(buyerId))
                .execute();
    }

    @Override
    public void deleteUserNotEmailAuth(long userId) {
        queryFactory
                .delete(user)
                .where(user.id.eq(userId))
                .execute();
    }
}
