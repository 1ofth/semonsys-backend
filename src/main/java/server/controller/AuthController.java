package server.controller;

import lombok.extern.java.Log;
import server.model.User;
import server.security.JwtManager;
import server.service.db.UserService;
import server.service.logic.TokensService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.text.ParseException;
import java.util.Date;

@Stateless
@Path("auth")
@Log
public class AuthController {

    @Context
    private SecurityContext securityContext;

    @Inject
    private UserService userService;

    @Inject
    private JwtManager jwtManager;

    @Inject
    private TokensService tokensService;

    @POST
    @Path("/logout")
    public Response logout() {
        tokensService.clearRefreshTokens(securityContext.getUserPrincipal().getName());
        return Response.ok("{message: 'logged out'}").build();
    }

    @POST
    @Path("/refresh-tokens")
    public Response refresh(@FormParam("refreshToken") String refreshToken,
                            @Context HttpServletResponse response) {
        User user = userService.findOne(securityContext.getUserPrincipal().getName());
        if (user.getRefreshTokens().remove(refreshToken)) {
            try {
                if (((Date) jwtManager.getClaims(refreshToken).get("exp")).after(new Date())) {
                    return tokensService.generateTokens(user);
                }
            } catch (ParseException e) {
                log.warning(e.toString());
                return Response.status(400).build();
            }
        }
        return Response.status(400).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("login") String login,
                          @FormParam("password") String password,
                          @Context HttpServletResponse response) {
        log.info("Authenticating " + login);
        User user = userService.findOne(login);
        if (user != null && user.getPassword().equals(password) && user.isConfirmed()) {
            return tokensService.generateTokens(user);
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
