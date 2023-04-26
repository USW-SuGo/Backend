package com.usw.sugo.domain.user.user.repository;

import static com.usw.sugo.domain.user.user.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.user.user.QUser;
import com.usw.sugo.domain.user.user.User;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public String findToAvailableNicknameNumber(String department) {
        return queryFactory
            .select(user.nickname)
            .from(user)
            .where(user.nickname.contains(department))
            .orderBy(user.nickname.desc())
            .limit(1)
            .fetchOne();
    }

    @Override
    public void deleteByUser(User inputUser) {
        queryFactory
            .delete(user)
            .where(user.id.eq(inputUser.getId()))
            .execute();
    }
}
