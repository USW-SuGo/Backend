package com.usw.sugo.global.util.nickname;

import static com.usw.sugo.global.exception.ExceptionType.INVALID_DEPARTMENT;

import com.usw.sugo.domain.user.user.repository.UserRepository;
import com.usw.sugo.global.exception.CustomException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NicknameGenerator {

    private final UserRepository userRepository;

    private static final List<String> departmentList = Stream.of(Department.values())
        .map(Department::getDepartment)
        .collect(Collectors.toList());

    private static void validateDepartment(String department) {
        if (!departmentList.contains(department)) {
            throw new CustomException(INVALID_DEPARTMENT);
        }
    }

    private Long generateNicknameNumber(String department) {
        String toAvailableNicknameNumber = userRepository.findToAvailableNicknameNumber(department);
        if (toAvailableNicknameNumber == null) {
            return 1L;
        }
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
        Long currentLongNumber = Long.parseLong(
            toAvailableNicknameNumber.substring(indexOfHyphen + 1,
                fetchToStringLengthAfterFiltering));
        return currentLongNumber + 1;
    }

    public String generateNickname(String department) {
        validateDepartment(department);
        return department + "-" + generateNicknameNumber(department);
    }
}
