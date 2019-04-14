package server.service.logic;

import lombok.Setter;
import server.model.Role;
import server.model.User;
import server.security.JwtManager;
import server.service.db.UserService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;

@Stateless
public class TokensService {
    private static final int SECONDS_IN_FIVE_HOURS = 5 * 60 * 60;
    private static final int MILLISECONDS_IN_SECOND = 1000;

    @Setter
    @Inject
    private JwtManager jwtManager;

    @Setter
    @Inject
    private UserService userService;

    public void clearRefreshTokens(final String login) {
        User user = userService.findOne(login);
        if (user != null) {
            user.getRefreshTokens().clear();
            userService.saveUser(user);
        }
    }

    public Response generateTokens(final User user) {
        String token = jwtManager.createAccessToken(user.getLogin(), new String[]{Role.USER});
        String refreshToken = jwtManager.createRefreshToken(user.getLogin());
        user.getRefreshTokens().add(refreshToken);
        userService.saveUser(user);
        JsonObject result = Json.createObjectBuilder()
                .add("accessToken", token)
                .add("refreshToken", refreshToken)
                .add("expires_in", System.currentTimeMillis() / MILLISECONDS_IN_SECOND
                    + SECONDS_IN_FIVE_HOURS)
                .build();
        return Response.ok(result).header("Authorization", "Bearer " + token).build();
    }
}
