package com.usw.sugo.domain.majoruser.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinFormRequest;
import com.usw.sugo.global.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import static com.usw.sugo.domain.majoruser.QUser.user;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;
    private final BCryptPasswordEncoder encoder;

    @Override
    public void authorizeToken(Long id) {
        queryFactory
                .update(user)
                .set(user.status, String.valueOf(Status.AVAILABLE))
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void detailJoin(DetailJoinFormRequest detailJoinFormRequest) {
        queryFactory
                .update(user)
                .set(user.password, encoder.encode(detailJoinFormRequest.getPassword()))
                .set(user.nickname, "닉네임 자동발급 로직 필요함")
                .where(user.email.eq(detailJoinFormRequest.getEmail()))
                .execute();
    }
}
