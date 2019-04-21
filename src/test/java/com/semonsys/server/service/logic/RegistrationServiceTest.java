package com.semonsys.server.service.logic;

import com.semonsys.server.model.dao.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RegistrationServiceTest {

    private RegistrationService registrationService;
    private MailService mailService = mock(MailService.class);
    private TokensService tokensService = mock(TokensService.class);

    @Before
    public void setUp() {
        this.registrationService = new RegistrationService();
        this.registrationService.setMailService(this.mailService);
        this.registrationService.setTokensService(this.tokensService);
    }


    @Test
    public void sendConfirmationMessage() {

        User expected = new User("123", "345", "mailTo@gmail.com",
            new ArrayList<>(Collections.singletonList(("my-test-token"))), false, null);
        this.registrationService.sendConfirmationMessage(expected, "test");

        verify(this.tokensService).generateVerificationToken(expected);
        verify(this.mailService).send(ArgumentMatchers.anyString(), eq(expected.getEmail()));
    }
}