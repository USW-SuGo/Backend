package com.usw.sugo.global.util.nickname;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum Department {

    KOREAN("국어국문학"),

    HISTORY("사학"),

    ENGLISH("영어영문학"),

    RUSSIAN("러시아어문학"),

    JAPANESES("일어일문학"),

    CHINESE("중어국문학"),

    LAW("법학"),

    ADMINISTRATION("행정학"),

    FIRE_ADMINISTRATION_NIGHT("소방행정학과(야)"),

    ECONOMICS_FINANCE("경제금융학과"),

    INTERNATIONAL_DEVELOPMENT_COOPERATION("국제개발협력학과"),

    BUSINESS_ADMINISTRATION("경영학과"),

    ACCOUNTING("회계학과"),

    HOTEL_MANAGEMENT("호텔경영학과"),

    FOOD_SERVICE_MANAGEMENT("외식경영학과"),

    BIOTECHNOLOGY_AND_MARKETING("바이오공학 및 마케팅"),

    CONVERGENCE_CHEMICAL_INDUSTRY("융합화학산업"),

    CIVIL_ENVIRONMENTAL_ENGINEERING("건설환경공학"),

    ENVIRONMENTAL_ENERGY_ENGINEERING("환경에너지공학"),

    ARCHITECTURE("건축학과"),

    URBAN_REAL_ESTATE("도시부동산학과"),

    INDUSTRIAL_ENGINEERING("산업공학과"),

    MECHANICAL_ENGINEERING("기계공학과"),

    ELECTRONIC_MATERIALS_ENGINEERING("전자재료공학"),

    ELECTRICAL_ENGINEERING("전기공학과"),

    ELECTRIC_ENGINEERING("전자공학과"),

    MATERIALS_SCIENCE_ENGINEERING("신소재공학과"),

    CHEMICAL_ENGINEERING("화학공학과"),

    COMPUTER_SW("컴퓨터SW학과"),

    MEDIA_SW("미디어SW학과"),

    INFORMATION_COMMUNICATION_ENGINEERING("정보통신공학과"),

    INFORMATION_SECURITY("정보보호학과"),

    NURSING("간호학과"),

    CHILD_FAMILY_WELFARE("아동가족복지학과"),

    CLOTHING("의류학과"),

    FOOD_NUTRITION("식품영양학과"),

    PHYSICAL_EDUCATION("체육학과"),

    LEISURE_SPORTS("레저스포츠학과"),

    EXERCISE_HEALTH_MANAGEMENT("운동건강관리학과"),

    PAINTING("회화과"),

    SCULPTURE("조소과"),

    COMMUNICATION_DESIGN("커뮤니케이션디자인학과"),

    FASHION_DESIGN("패션디자인학과"),

    CRAFT_DESIGN("공예디자인학과"),

    COMPOSITION("작곡과"),

    VOCAL_MUSIC("성악과"),

    PIANO("피아노과"),

    ORCHESTRA("관현악과"),

    KOREAN_ORCHESTRA("국악과"),

    FILM_VIDEO("영화영상과"),

    THEATER("연극과"),

    CULTURAL_CONTENTS_TECHNOLOGY("문화컨텐츠테크놀러지학과"),

    CLOUD_CONVERGENCE("클라우드융복합학과"),

    SYSTEM_SEMICONDUCTOR_CONVERGENCE("시스템반도체융복합학과");

    private final String department;
}