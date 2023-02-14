package com.usw.sugo.global.aws.ses.mailform;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmailForm {

    public String setStudentAuthFormSubject() {
        return "SUGO - 재학생 이메일 인증 요청입니다.";
    }

    public String setStudentFindLoginIdSubject() {
        return "SUGO - 아이디 찾기 결과 안내입니다.";
    }

    public String setStudentFindPasswordSubject() {
        return "SUGO - 비밀번호 찾기 결과 안내입니다.";
    }


    public String buildStudentAuthForm(String payload) {
        return "<center>\n" +
            "\t<img class=\"sugologo\"src=\"https://avatars.githubusercontent.com/u/112926733?s=200&v=4\" style=\"display:block; \"alt=\"SUGOLOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 중고거래 플랫폼 SUGO 입니다.\n" +
            "\t\t<p>\n" +
            "                <b>재학생 인증 메일 전송해드립니다. </b>\n" +
            "\t\t<br>\n" +
            "\t\t" + payload + "\n" +
            "                <p>\n" +
            "<br>" +
            "                <b>회원가입 화면</b>에서 위 <b>인증번호를 입력</b>하시면 정상적으로 서비스 이용이 가능합니다." + "\n" +
            "<br>" +
            "\t\t인증이 정상적으로 수행되지 않을 시, sugousw@gmail.com 으로 문의 부탁드립니다. \n" +
            "\t\t<p>\n" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }

    public String buildFindLoginIdForm(String loginId) {
        return "<center>\n" +
            "\t<img class=\"sugologo\"src=\"https://avatars.githubusercontent.com/u/112926733?s=200&v=4\" style=\"display:block; \"alt=\"SUGOLOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 중고거래 플랫폼 SUGO 입니다.\n" +
            "\t\t<p>\n" +
            "                <b>아이디 찾기 결과를 안내해드립니다. </b>\n" +
            "\t\t<p>\n" +
            "\t\t" + loginId + "\n" +
            "                <p>\n" +
            "\t\t 기타 문의사항이 있으시다면, sugousw@gmail.com 으로 문의 부탁드립니다. \n" +
            "\t\t<p>\n" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }

    public String buildFindPasswordForm(String newPassword) {
        return "<center>\n" +
            "\t<img class=\"sugologo\"src=\"https://avatars.githubusercontent.com/u/112926733?s=200&v=4\" style=\"display:block; \"alt=\"SUGOLOGO\">"
            +
            "\t<div class=container>\n" +
            "\t\t안녕하세요. 수원대학교 중고거래 플랫폼 SUGO 입니다.\n" +
            "\t\t<p>\n" +
            "                <b>비밀번호 찾기 결과를 안내해드립니다. </b>\n" +
            "\t\t<p>\n" +
            "\t\t" + newPassword + "\n" +
            "                <p>\n" +
            "                해당 비밀번호로 로그인 하신 후 <br>반드시 비밀번호를 변경해주시길 바랍니다.</b>" + "\n" +
            "\t\t 기타 문의사항이 있으시다면, sugousw@gmail.com 으로 문의 부탁드립니다. \n" +
            "\t\t<p>\n" +
            "\t\t감사합니다.\n" +
            "\t</div>\n" +
            "</center>";
    }
}
