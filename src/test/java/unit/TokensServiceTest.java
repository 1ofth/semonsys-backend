package unit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import server.model.User;
import server.security.JwtManager;
import server.service.db.UserService;
import server.service.logic.TokensService;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TokensServiceTest {

    private TokensService tokensService;
    private UserService userService = mock(UserService.class);
    private JwtManager jwtManager = mock(JwtManager.class);

    @Before
    public void setUp() {
        this.tokensService = new TokensService();
        this.tokensService.setJwtManager(jwtManager);
        this.tokensService.setUserService(userService);
    }

    @Test
    public void clearRefreshTokens() {
        User expected = new User("123", "345", null,
                new ArrayList<>(Collections.singletonList(("my-test-token"))), true, null);

        when(this.userService.findOne(ArgumentMatchers.anyString()))
                .thenReturn(expected);
        doNothing().when(this.userService).update(ArgumentMatchers.any());
        this.tokensService.clearRefreshTokens(expected.getLogin());
        assertEquals(Collections.emptyList(), expected.getRefreshTokens());
        verify(this.userService).update(ArgumentMatchers.any());
    }

    @Test
    public void generateTokens() {
        User expected = new User("123", "345", null,
                new ArrayList<>(Collections.singletonList(("my-test-token"))), true, null);
        doNothing().when(this.userService).update(ArgumentMatchers.any());

        when(this.jwtManager.createAccessToken(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn("access-token");

        when(this.jwtManager.createRefreshToken(ArgumentMatchers.anyString()))
                .thenReturn("refresh-token");
        Response response = this.tokensService.generateTokens(expected);
        assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        verify(this.userService).update(ArgumentMatchers.any());

    }

}