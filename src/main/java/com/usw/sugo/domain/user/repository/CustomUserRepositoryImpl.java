package com.usw.sugo.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.user.User;
import com.usw.sugo.domain.user.dto.QUserResponseDto_UserPageResponseForm;
import com.usw.sugo.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.usw.sugo.domain.user.QUser.user;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;
    private final BCryptPasswordEncoder encoder;

    @Override
    public UserResponseDto.UserPageResponseForm loadUserPage(User requestUser) {
        UserResponseDto.UserPageResponseForm userPageResponseForm = queryFactory
                .select(new QUserResponseDto_UserPageResponseForm(
                        user.id.as("userId"), user.email, user.nickname, user.mannerGrade,
                        user.countMannerEvaluation, user.countTradeAttempt))
                .from(user)
                .where(user.id.eq(requestUser.getId()))
                .fetchOne();
        return userPageResponseForm;
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
}
