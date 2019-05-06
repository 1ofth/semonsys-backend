package com.semonsys.server.service.logic;

import lombok.extern.java.Log;

import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Stateless
@Log
public class MailService {

    private static final String SENDER_EMAIL = "semonsys.mailer@yandex.ru";
    private static final String SENDER_PASSWORD = "WtJ&qqVer+W)9n/b";

    private static final Authenticator AUTHENTICATOR = new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
        }
    };

    public void send(final String body, final String url) {
        Properties props = new Properties();
        try (InputStream s = MailService.class.getResourceAsStream("/server.properties")) {
            props.load(s);
        } catch (IOException e) {
            log.warning(e.getMessage());
        }

        Session session = Session.getInstance(props, AUTHENTICATOR);

        MimeMessage message = new MimeMessage(session);
        try {
            message.setContent(body, "text/html; charset=utf-8");
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, url);
            message.setSubject("Registration Confirmation");
            Transport.send(message);
        } catch (MessagingException e) {
            log.warning(e.getMessage());
        }
    }
}
