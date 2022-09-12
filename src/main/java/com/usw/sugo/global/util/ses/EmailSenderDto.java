package com.usw.sugo.global.util.ses;

import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import lombok.Builder;

@Builder
public class EmailSenderDto extends SendEmailRequest {

    public String source;
    public String subject;
    public String message;
}
