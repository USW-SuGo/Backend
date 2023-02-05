# 세부 기술스택

|    Framework    |           DB Access           |    DBMS     | Storage | Domain  | MailSender |
|:---------------:|:-----------------------------:|:-----------:|:-------:|:-------:|:----------:|
| Java/SpringBoot | Spring Data JPA <br> QueryDSL | RDS(MySQL)  |   S3    | Route53 |    SES     |

<br>

|                     Deploy Environment                     |
|:----------------------------------------------------------:|
| EC2(Ubuntu 20.04) <br> Gradle 7.5.2 <br> JDK 11 |

---

# ERD (Ver. 22/11/21)

![](src/main/resources/erd/SUGO%20ERD-6.png)

---

# API 명세서 (Ver. 22/11/21)

[API 명세서 (Ver. 22/11/05)](https://diger.gitbook.io/untitled-1/)

---

## 프로젝트간 개인 목표

[이전 프로젝트](https://github.com/uswLectureEvaluation/Backend-Remaster)에서 작성한 방식말고, 더 향상된 코드 작성을 위한 연습요소를 포함하기.

[스터디-자바와깐부맺기](https://github.com/Be-GGanboo-With-Java)에서 학습한 내용 적극적으로 반영해보기.

1. QueryDSL
2. JWT Filter 적용
3. Spring Security 로 인증 인가 관리
4. Clean Code Convention
