package com.usw.sugo.domain.majoruser.user.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.usw.sugo.domain.majoruser.QUser.user;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomUserRepositoryImplTest {

    @Autowired
    JPAQueryFactory queryFactory;

    @Test
    void authorizeToken() {
    }

    @Test
    void detailJoin() {
    }

    @Test
    void editPassword() {
    }

    @Test
    void editNickname() {
    }

    @Test
    void setModifiedDate() {
    }

    @Test
    void findNicknameNumber() {

        String department = "정보통신공학과";

        List<String> fetch = queryFactory
                .select(user.nickname)
                .from(user)
                .where(user.nickname.contains(department))
                .orderBy(user.nickname.desc())
                .limit(1)
                .fetch();


        if (fetch.isEmpty()) {
            System.out.println(0);
        }

        else if (fetch.toString().length() != 0) {
            StringBuilder fetchToString = new StringBuilder(fetch.toString());
            int fetchToStringLength = fetchToString.length();
            int indexOfHipen = 0;

            // 대괄호 제거
            fetchToString.delete(0, 1);
            fetchToString.delete(fetchToStringLength - 2, fetchToStringLength - 1);

            for (int i = 0; i < fetchToStringLength; i++) {
                if (fetchToString.charAt(i) == '-') {
                    indexOfHipen = i;
                    break;
                }
            }

            // 필터링 후 문자열 길이 다시 계산
            int fetchToStringLengthAfterFiltering = fetchToString.length();

            long currentLongNumber = Long.parseLong(
                    fetchToString.substring(indexOfHipen + 1, fetchToStringLengthAfterFiltering));

            System.out.println(currentLongNumber + 1);
        }
    }
}