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
    @Setter
    @Inject
    private JwtManager jwtManager;

    @Setter
    @Inject
    private UserService userService;

    public void clearRefreshTokens(String login) {
        User user = userService.findOne(login);
        if (user != null) {
            user.getRefreshTokens().clear();
            userService.update(user);
        }
    }

    public Response generateTokens(User user) {
        String token = jwtManager.createAccessToken(user.getLogin(), new String[]{Role.USER});
        String refreshToken = jwtManager.createRefreshToken(user.getLogin());
        user.getRefreshTokens().add(refreshToken);
        userService.update(user);
        JsonObject result = Json.createObjectBuilder()
                .add("accessToken", token)
                .add("refreshToken", refreshToken)
                .add("expires_in",   (System.currentTimeMillis() / 1000) + 14400)
                .build();
        return Response.ok(result).header("Authorization", "Bearer " + token).build();
    }
}
