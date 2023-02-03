package com.usw.sugo.domain.user.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.usw.sugo.domain.user.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static com.usw.sugo.domain.user.user.QUser.user;

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
    public void setRecentUpPostingDate(Long id) {
        queryFactory
                .update(user)
                .set(user.recentUpPost, LocalDateTime.now())
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void deleteByUser(User requestUser) {
        queryFactory
                .delete(user)
                .where(user.id.eq(requestUser.getId()))
                .execute();
    }
}
