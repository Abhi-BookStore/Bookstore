package com.bookstore.service.impl;

import com.bookstore.service.AsyncEmailService;
import com.bookstore.service.EmailTemplateService;
import com.sendgrid.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@PropertySource(value="classpath:application.properties")
public class AsyncEmailServiceImpl implements AsyncEmailService {

    @Value("#{'${sendgrid_api_key}'}")
    private String sendgridApiKey;

    @Autowired
    private EmailTemplateService service;

    private static final String EMAIL_ENDPOINT = "/mail/send";

    /**
     * Sends an email message asynchronously through SendGrid.
     * Status Code: 202	- ACCEPTED: Your message is both valid, and queued to be delivered.
     *
     * @param from      email address from which the message will be sent.
     * @param recipient String containing the recipients of the message.
     * @param subject   subject header field.
     * @param text      content of the message.
     */
    @Override
    @Async
    public void send(String from, String recipient, String replyTo, String subject, String text) throws IOException {
        Email emailFrom = new Email(from);
        String emailSubject = subject;
        Email emailTo = new Email(recipient);

        Content emailContent = new Content("text/html", text);
        Mail mail = new Mail(emailFrom, emailSubject, emailTo, emailContent);
        if(!replyTo.isEmpty()){
            Email emailReplyTo = new Email(replyTo);
            mail.setReplyTo(emailReplyTo);
        }

        SendGrid sendGrid = new SendGrid(sendgridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint(EMAIL_ENDPOINT);
        request.setBody(mail.build());

        Response  response = sendGrid.api(request);
        System.out.println("API KEY :::: "+ sendgridApiKey);
        System.out.println("::::::::::: SendGrid email response code ::: " + response.getStatusCode());
    }

    /**
     * Sends email using variable automatically inserted into     *
     *
     * @param from
     * @param recipient
     * @param replyTo
     * @param subject
     * @param template
     * @param mailContext
     */
    @Override
    public void sendWithContext(String from, String recipient, String replyTo, String subject, String template, Map<String, Object> mailContext) {

        String text = service.generateFromContext(mailContext,template);
        try{
            send(from, recipient, replyTo, subject, text);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
