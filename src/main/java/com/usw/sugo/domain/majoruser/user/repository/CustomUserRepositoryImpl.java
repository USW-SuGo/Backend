package com.usw.sugo.domain.majoruser.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinRequest;
import com.usw.sugo.global.status.Status;
import com.usw.sugo.global.util.nickname.NicknameGenerate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static com.usw.sugo.domain.majoruser.QUser.user;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;
    private final BCryptPasswordEncoder encoder;
    private final NicknameGenerate nicknameGenerate;

    @Override
    public void authorizeToken(Long id) {
        queryFactory
                .update(user)
                .set(user.status, String.valueOf(Status.AVAILABLE))
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void detailJoin(DetailJoinRequest detailJoinRequest, Long userId) {

        queryFactory
                .update(user)
                .set(user.password, encoder.encode(detailJoinRequest.getPassword()))
                .set(user.nickname, nicknameGenerate.generateNickname(userId, detailJoinRequest.getDepartment()))
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

    @Override
    public void editNickname(Long id, String nickName) {
        queryFactory
                .update(user)
                .set(user.nickname, nickName)
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void setModifiedDate(Long id) {
        queryFactory
                .update(user)
                .set(user.modifiedDate, LocalDateTime.now())
                .where(user.id.eq(id))
                .execute();
    }
}
