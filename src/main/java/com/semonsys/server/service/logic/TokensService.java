package com.semonsys.server.service.logic;

import com.semonsys.server.model.dao.Role;
import com.semonsys.server.model.dao.User;
import com.semonsys.server.security.JwtManager;
import com.semonsys.server.service.db.UserService;
import lombok.Setter;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Stateless
public class TokensService {
    @Setter
    @Inject
    private JwtManager jwtManager;

    @Setter
    @Inject
    private UserService userService;

    public void generateVerificationToken(final User user) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userService.update(user);
    }


    public void clearRefreshTokens(final String login) {
        User user = userService.find(login);
        if (user != null) {
            user.getRefreshTokens().clear();
            userService.update(user);
        }
    }

    public Response generateTokens(final User user) {
        String token = jwtManager.createAccessToken(user.getLogin(), new String[]{Role.USER});
        String refreshToken = jwtManager.createRefreshToken(user.getLogin());
        user.getRefreshTokens().add(refreshToken);
        userService.update(user);
        JsonObject result = Json.createObjectBuilder()
            .add("accessToken", token)
            .add("refreshToken", refreshToken)
            .add("expires_in", System.currentTimeMillis() / JwtManager.MILLISECONDS_IN_SECOND + JwtManager.SECONDS_IN_FIVE_HOURS)
            .build();
        return Response.ok(result).header("Authorization", "Bearer " + token).build();
    }
}
