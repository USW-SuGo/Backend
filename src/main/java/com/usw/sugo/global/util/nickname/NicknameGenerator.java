package com.usw.sugo.global.util.nickname;

import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.usw.sugo.global.exception.ExceptionType.INVALID_DEPARTMENT;

@Service
@RequiredArgsConstructor
public class NicknameGenerator {

    private static UserRepository userRepository;

    private static final List<String> departmentList = Stream.of(Department.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    private static void validateDepartment(String department) {
        if (!departmentList.contains(department)) {
            throw new CustomException(INVALID_DEPARTMENT);
        }
    }

    private static long generateNicknameNumber(String department) {
        String toAvailableNicknameNumber = userRepository.findToAvailableNicknameNumber(department);
        int fetchToStringLength = toAvailableNicknameNumber.length();

        // 하이픈 인덱스
        int indexOfHyphen = 0;

        // 하이픈 인덱스 기록
        for (int i = 0; i < fetchToStringLength; i++) {
            if (toAvailableNicknameNumber.charAt(i) == '-') {
                indexOfHyphen = i;
                break;
            }
        }
        int fetchToStringLengthAfterFiltering = toAvailableNicknameNumber.length();
        long currentLongNumber = Long.parseLong(
                toAvailableNicknameNumber.substring(indexOfHyphen + 1, fetchToStringLengthAfterFiltering));

        return currentLongNumber + 1;
    }

    public static String generateNickname(String department) {
        validateDepartment(department);
        return department + "-" + generateNicknameNumber(department);
    }
}
