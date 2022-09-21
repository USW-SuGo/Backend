package com.usw.sugo.global.aws.ses;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AuthSuccessViewForm {

    public String successParagraph() {
        return "<center>\n" +
                "\t<div class=\"container\">\n" +
                "  \n" +
                "\t\t<h2>수원대학교 재학생 인증에 성공하셨습니다. <br> 추가 회원 정보를 입력하시면 서비스를 이용하실 수 있습니다!</h2>\n" +
                "\t</div>\n" +
                "\n" +
                "</center>";
    }
}
