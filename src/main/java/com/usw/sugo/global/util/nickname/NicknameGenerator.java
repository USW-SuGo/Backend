package com.usw.sugo.global.util.nickname;

import com.usw.sugo.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.usw.sugo.global.exception.ErrorCode.INVALID_DEPARTMENT;

@Service
@RequiredArgsConstructor
public class NicknameGenerator {

    private final NicknameNumberGenerator nicknameNumberGenerator;

    public static List<String> departmentList = new ArrayList<>(){{
        add("국어국문학");
        add("사학");
        add("영어영문학");
        add("러시아어문학");
        add("일어일문학");
        add("중어국문학");
        add("법학");
        add("행정학");
        add("소방행정학과(야)");
        add("경제금융학과");
        add("국제개발협력학과");
        add("경영학과");
        add("회계학과");
        add("호텔경영학과");
        add("외식경영학과");
        add("바이오공학 및 마케팅");
        add("융합화학산업");
        add("건설환경공학");
        add("환경에너지공학");
        add("건축학과");
        add("도시부동산학과");
        add("산업공학과");
        add("기계공학과");
        add("전자재료공학");
        add("전기공학과");
        add("전자공학과");
        add("신소재공학과");
        add("화학공학과");
        add("컴퓨터SW학과");
        add("미디어SW학과");
        add("정보통신공학과");
        add("정보보호학과");
        add("간호학과");
        add("아동가족복지학과");
        add("의류학과");
        add("식품영양학과");
        add("체육학과");
        add("레저스포츠학과");
        add("운동건강관리학과");
        add("회화과");
        add("조소과");
        add("커뮤니케이션디자인학과");
        add("패션디자인학과");
        add("공예디자인학과");
        add("작곡과");
        add("성악과");
        add("피아노과");
        add("관현악과");
        add("국악과");
        add("영화영상과");
        add("연극과");
        add("문화컨텐츠테크놀러지학과");
        add("클라우드융복합학과");
        add("시스템반도체융복합학과");
    }};

    public void validateDepartment(String department) {
        if (!departmentList.contains(department)) {
            throw new CustomException(INVALID_DEPARTMENT);
        }
    }

    public String generateNickname(String department) {
        if (!departmentList.contains(department)) {
            throw new CustomException(INVALID_DEPARTMENT);
        }

        return department + "-" + nicknameNumberGenerator.findToAvailableNicknameNumber(department);
    }
}
