package com.usw.sugo.domain.majoruser.user.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinRequest;
import com.usw.sugo.domain.status.Status;
import com.usw.sugo.global.util.nickname.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.usw.sugo.domain.majoruser.QUser.user;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;
    private final BCryptPasswordEncoder encoder;
    private final NicknameGenerator nicknameGenerator;

    @Override
    public void modifyingStatusToAvailable(Long id) {
        queryFactory
                .update(user)
                .set(user.status, Status.AVAILABLE)
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void detailJoin(DetailJoinRequest detailJoinRequest, Long userId) {
        queryFactory
                .update(user)
                .set(user.password, encoder.encode(detailJoinRequest.getPassword()))
                .set(user.nickname, nicknameGenerator.generateNickname(userId, detailJoinRequest.getDepartment()))
                .set(user.mannerGrade, BigDecimal.ZERO)
                .set(user.countMannerEvaluation, 0L)
                .set(user.recentUpPost, LocalDateTime.now().minusDays(1))
                .set(user.recentEvaluationManner, LocalDateTime.now().minusDays(1))
                .where(user.email.eq(detailJoinRequest.getEmail()))
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

    // 리팩터링 필요*
    @Override
    public void setModifiedDate(Long id) {
        queryFactory
                .update(user)
                .set(user.updatedAt, LocalDateTime.now())
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void findNicknameNumber(String department) {
        JPAQuery<String> select = queryFactory
                .select(user.nickname)
                .from(user)
                .where(user.nickname.like(department))
                .orderBy(user.nickname.desc())
                .limit(1);

        System.out.println(select);
    }

    @Override
    public void setMannerGrade(BigDecimal grade, long targetUserId, long evaluatingUserId) {

        // 매너평가 덧셈 및 카운트 증가
        queryFactory
                .update(user)
                .set(user.mannerGrade, (user.mannerGrade.add(grade)).divide(user.countMannerEvaluation))
                .set(user.countMannerEvaluation, user.countMannerEvaluation.add(1))
                .where(user.id.eq(targetUserId))
                .execute();

        // 하루에 한 번만 평가할 수 있도록 쿨타임 시작
        queryFactory
                .update(user)
                .set(user.recentEvaluationManner, LocalDateTime.now())
                .where(user.id.eq(evaluatingUserId))
                .execute();
    }


}
