package com.semonsys.server.service.logic;

import com.semonsys.server.model.dao.User;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Stateless
@Log
public class RegistrationService {
    @Setter
    @Inject
    private MailService mailService;

    @Setter
    @Inject
    private TokensService tokensService;

    public void sendConfirmationMessage(final User user, final String appUrl) {
        tokensService.generateVerificationToken(user);
        String url = appUrl + "/confirm/" + user.getVerificationToken();
        Properties props = new Properties();
        try (InputStream s = MailService.class.getResourceAsStream("/server.properties")) {
            props.load(s);
        } catch (IOException e) {
            log.warning(e.getMessage());
        }

        String message = props.getProperty("confirm.message") + "<a href=\"" + url + "\"> link </a>";
        mailService.send(message, user.getEmail());
    }
}
