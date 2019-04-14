package com.semonsys.server.service.logic;

import com.semonsys.server.model.User;
import lombok.Setter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Properties;

@Stateless
public class RegistrationService {
    @Setter
    @Inject
    private MailService mailService;

    @Setter
    @Inject
    private TokensService tokensService;

    public void sendConfirmationMessage(User user, String appUrl) {
        tokensService.generateVerificationToken(user);
        String url = appUrl + "/confirm/" + user.getVerificationToken();
        Properties props = new Properties();
        try {
            props.load(RegistrationService.class.getResourceAsStream("/server.properties"));
        } catch (IOException ignored) {
        }
        String message = props.getProperty("confirm.message") + "<a href=\"" + url + "\"> link </a>";
        mailService.send(message, user.getEmail());
    }
}
