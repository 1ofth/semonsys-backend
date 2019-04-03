package server.service.logic;

import lombok.Setter;
import lombok.extern.java.Log;
import server.model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

@Stateless
@Log
public class MailService {
    @Setter
    @Inject
    private TokensService tokensService;

    private static final String senderEmail = "semonsys.mailer@yandex.ru";
    private static final String senderPassword = "WtJ&qqVer+W)9n/b";

    public void sendTo(User user, String appUrl) {
        tokensService.generateVerificationToken(user);
        String url = appUrl + "/confirm/" + user.getVerificationToken();
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
            message.setContent(props.getProperty("confirm.message") + "<a href=\"" + url + "\"> link </a>",
                    "text/html; charset=utf-8");
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, user.getEmail());
            message.setSubject("Registration Confirmation");
            Transport.send(message);
        } catch (MessagingException e) {
            log.warning(e.getMessage());
        }
    }
}
