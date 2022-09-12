package com.usw.sugo.global.util.ses;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AuthEmailForm {

    public String setSubject() {
        return "SUGO - 재학생 이메일 인증 요청입니다.";
    }

    public String buildContentWithLink(String link) {
        return "<center>\n" +
                "\t<div class=\"container\">\n" +
                "  \n" +
                "\t\t<h2>수원대학교 재학생 인증 메일입니다.</h2>\n" +
                "  \n" +
                "\t\t<hr>\n" +
                "  \t\t<br>\n" +
                "  \t\t<a href=\"" + link + "\">링크를 클릭하시면 인증 요청이 수행됩니다.</a> " +
                "\t</div>\n" +
                "\n" +
                "  <p><br />인증이 정상적으로 수행되지 않을 시, uswsugo@gmail.com 으로 문의 부탁드립니다. 감사합니다.</p>\n" +
                "</center>";
    }
}
