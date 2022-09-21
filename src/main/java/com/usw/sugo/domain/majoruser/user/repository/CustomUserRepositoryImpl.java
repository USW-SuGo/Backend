package com.usw.sugo.domain.majoruser.user.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.majoruser.User;
import com.usw.sugo.domain.majoruser.user.dto.UserRequestDto.DetailJoinRequest;
import com.usw.sugo.domain.status.Status;
import com.usw.sugo.global.util.nickname.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

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
    public User findByEmailForUserDetails(String email) {
        List<String> fetch = queryFactory
                .select(user.email)
                .from(user)
                .where(user.email.eq(email))
                .fetch();

        return (User) fetch;
    }

}
