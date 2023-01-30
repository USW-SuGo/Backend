package com.usw.sugo.global.util.nickname;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.usw.sugo.domain.user.QUser.user;

@Service
@RequiredArgsConstructor
public class NicknameNumberGenerator {

    private final JPAQueryFactory queryFactory;

    public long findToAvailableNicknameNumber(String department) {

        List<String> fetch = queryFactory
                .select(user.nickname)
                .from(user)
                .where(user.nickname.contains(department))
                .orderBy(user.nickname.desc())
                .limit(1)
                .fetch();

        if (fetch.isEmpty()) {
            return 1L;
        }

        StringBuilder fetchToString = new StringBuilder(fetch.toString());

        // 문자열 길이
        int fetchToStringLength = fetchToString.length();

        // 대괄호 제거
        fetchToString.delete(0, 1);
        fetchToString.delete(fetchToStringLength - 2, fetchToStringLength - 1);

        // 하이픈 인덱스
        int indexOfHyphen = 0;

        // 하이픈 인덱스 기록
        for (int i = 0; i < fetchToStringLength; i++) {
            if (fetchToString.charAt(i) == '-') {
                indexOfHyphen = i;
                break;
            }
        }

        // 필터링 후 문자열 길이 다시 계산
        int fetchToStringLengthAfterFiltering = fetchToString.length();

        // 하이픈 이후, 가장 마지막에 위치한 숫자 가져오기
        long currentLongNumber = Long.parseLong(
                fetchToString.substring(indexOfHyphen + 1, fetchToStringLengthAfterFiltering));

        return currentLongNumber + 1;
    }
}
