package com.usw.sugo.global.aws.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendEmailServiceFromSES {

    private final AuthEmailForm authEmailForm;

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    public void sendStudentAuthContent(String receivers, String link) {

        Destination destination = new Destination().withToAddresses(receivers);

        Message message = new Message()
                .withSubject(createContent(authEmailForm.setSubject()))
                .withBody(new Body()
                        .withHtml(createContent(authEmailForm.buildContentWithLink(link))));

        SendEmailRequest buildingRequest = new SendEmailRequest()
                .withSource("sugousw@gmail.com")
                .withDestination(destination)
                .withMessage(message);

        amazonSimpleEmailService.sendEmail(buildingRequest);
    }

    public void sendFindLoginIdResult(String receivers, String loginId) {

        Destination destination = new Destination().withToAddresses(receivers);

        Message message = new Message()
                .withSubject(createContent(authEmailForm.setSubject()))
                .withBody(new Body()
                        .withHtml(createContent(authEmailForm.buildContentByFindLoginId(loginId))));

        SendEmailRequest buildingRequest = new SendEmailRequest()
                .withSource("sugousw@gmail.com")
                .withDestination(destination)
                .withMessage(message);

        amazonSimpleEmailService.sendEmail(buildingRequest);
    }

    public void sendFindPasswordResult(String receivers, String newPassowrd) {

        Destination destination = new Destination().withToAddresses(receivers);

        Message message = new Message()
                .withSubject(createContent(authEmailForm.setSubject()))
                .withBody(new Body()
                        .withHtml(createContent(authEmailForm.buildContentByFindPasswordString(newPassowrd))));

        SendEmailRequest buildingRequest = new SendEmailRequest()
                .withSource("sugousw@gmail.com")
                .withDestination(destination)
                .withMessage(message);

        amazonSimpleEmailService.sendEmail(buildingRequest);
    }

    private Content createContent(String text) {
        return new Content()
                .withCharset("UTF-8")
                .withData(text);
    }
}
