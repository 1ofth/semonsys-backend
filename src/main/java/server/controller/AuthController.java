package server.controller;

import lombok.extern.java.Log;
import server.model.Role;
import server.model.User;
import server.security.JwtManager;
import server.service.UserService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Stateless
@Path("auth")
@Log
public class AuthController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Context
    private SecurityContext securityContext;

    @EJB
    private UserService userService;

    @EJB
    private JwtManager jwtManager;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postJWT(@FormParam("login") String login,
                            @FormParam("password") String password,
                            @Context HttpServletResponse response) {
        log.info("Authenticating " + login);
        try {
            User user = userService.findOne(login);
            if (user != null && user.getPassword().equals(password)) {
                if (user.getLogin() != null) {
                    log.info("Generating JWT for org.jboss.user " + user.getLogin());
                    String token = jwtManager.createJwt(user.getLogin(), new String[]{Role.USER});
                    response.setHeader(AUTHORIZATION_HEADER, BEARER + token);
                    JsonObject result = Json.createObjectBuilder()
                            .add("user", user.getLogin())
                            .build();
                    return Response.ok(result).build();
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
