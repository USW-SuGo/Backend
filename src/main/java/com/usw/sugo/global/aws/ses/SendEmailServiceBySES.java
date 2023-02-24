package com.usw.sugo.global.aws.ses;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.usw.sugo.global.aws.ses.mailform.EmailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendEmailServiceBySES {

    private final EmailForm emailForm;

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    public void sendStudentAuthContent(String receivers, String link) {

        final Destination destination = new Destination().withToAddresses(receivers);

        final Message message = new Message()
            .withSubject(createContent(emailForm.setStudentAuthFormSubject()))
            .withBody(new Body()
                .withHtml(createContent(emailForm.buildStudentAuthForm(link))));

        final SendEmailRequest buildingRequest = new SendEmailRequest()
            .withSource("sugousw@gmail.com")
            .withDestination(destination)
            .withMessage(message);

        amazonSimpleEmailService.sendEmail(buildingRequest);
    }

    public void sendFindLoginIdResult(String receivers, String loginId) {

        final Destination destination = new Destination().withToAddresses(receivers);

        final Message message = new Message()
            .withSubject(createContent(emailForm.setStudentFindLoginIdSubject()))
            .withBody(new Body()
                .withHtml(createContent(emailForm.buildFindLoginIdForm(loginId))));

        SendEmailRequest buildingRequest = new SendEmailRequest()
            .withSource("sugousw@gmail.com")
            .withDestination(destination)
            .withMessage(message);

        amazonSimpleEmailService.sendEmail(buildingRequest);
    }

    public void sendFindPasswordResult(String receivers, String newPassword) {

        final Destination destination = new Destination().withToAddresses(receivers);

        final Message message = new Message()
            .withSubject(createContent(emailForm.setStudentFindPasswordSubject()))
            .withBody(new Body()
                .withHtml(createContent(emailForm.buildFindPasswordForm(newPassword))));

        final SendEmailRequest buildingRequest = new SendEmailRequest()
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
