package com.semonsys.server.service.logic;

import lombok.extern.java.Log;

import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

@Stateless
@Log
public class MailService {

    private static final String senderEmail = "semonsys.mailer@yandex.ru";
    private static final String senderPassword = "WtJ&qqVer+W)9n/b";

    public void send(String body, String url) {
        Properties props = new Properties();
        try {
            props.load(MailService.class.getResourceAsStream("/server.properties"));
        } catch (IOException ignored) {
        }

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        try {
            message.setContent(body,"text/html; charset=utf-8");
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, url);
            message.setSubject("Registration Confirmation");
            Transport.send(message);
        } catch (MessagingException e) {
            log.warning(e.getMessage());
        }
    }
}
