package com.usw.sugo.global.aws.ses;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AuthEmailForm {

    public String setSubject() {
        return "SUGO - 재학생 이메일 인증 요청입니다.";
    }

    public String buildContentWithLink(String payload) {
        return "<center>\n" +
                "\t<img class=\"sugologo\"src=\"https://avatars.githubusercontent.com/u/112926733?s=200&v=4\" style=\"display:block; \"alt=\"SUGOLOGO\">" +
                "\t<div class=container>\n" +
                "\t\t안녕하세요. 수원대학교 중고거래 플랫폼 SUGO 입니다.\n" +
                "\t\t<p>\n" +
                "                <b>재학생 인증 메일 전송해드립니다. </b>\n" +
                "\t\t<p>\n" +
                "\t\t" + payload + "\n" +
                "                <p>\n" +
                "                <b>회원가입 화면</b>에서 위 <b>인증번호를 입력</b>하시면 정상적으로 서비스 이용이 가능합니다." + "\n" +
                "\t\t인증이 정상적으로 수행되지 않을 시, sugousw@gmail.com 으로 문의 부탁드립니다. \n" +
                "\t\t<p>\n" +
                "\t\t감사합니다.\n" +
                "\t</div>\n" +
                "</center>";
    }

    public String buildContentByFindLoginId(String loginId) {
        return "<center>\n" +
                "\t<div class=\"container\">\n" +
                "  \n" +
                "\t\t<h2>SUGO 아이디 찾기 결과입니다.</h2>\n" +
                "  \n" +
                "\t\t<hr>\n" +
                "  \t\t<br>\n" +
                "  \t\t" + loginId + "\n" + "요청하신 이메일에 해당하는 ID 는 위와 같습니다." +
                "\t</div>\n" +
                "\n" +
                "  <p><br />서비스 이용에 불편이 있으시면, sugousw@gmail.com 으로 문의 부탁드립니다. 감사합니다.</p>\n" +
                "</center>";
    }

    public String buildContentByFindPasswordString(String newPassword) {
        return "<center>\n" +
                "\t<div class=\"container\">\n" +
                "  \n" +
                "\t\t<h2>SUGO 비밀번호 찾기 요청 결과입니다.</h2>\n" +
                "  \n" +
                "\t\t<hr>\n" +
                "  \t\t<br>\n" +
                "  \t\t" + newPassword + "\n" + "비밀번호는 위와 같이 초기화 되었습니다. 로그인 후 비밀번호를 꼭 변경해주세요. " +
                "\t</div>\n" +
                "\n" +
                "  <p><br />서비스 이용에 불편이 있으시면, sugousw@gmail.com 으로 문의 부탁드립니다. 감사합니다.</p>\n" +
                "</center>";
    }
}
