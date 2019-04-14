package server.controller;

import lombok.extern.java.Log;
import server.model.User;
import server.security.JwtManager;
import server.service.db.UserService;
import server.service.logic.TokensService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.text.ParseException;
import java.util.Date;

@Stateless
@Path("/rest")
@Log
public class AuthController {

    @Context
    private SecurityContext securityContext;

    @EJB
    private UserService userService;

    @EJB
    private JwtManager jwtManager;

    @EJB
    private TokensService tokensService;

    @POST
    @Path("/secured/auth/logout")
    public Response logout() {
        tokensService.clearRefreshTokens(securityContext.getUserPrincipal().getName());
        return Response.ok("{message: 'logged out'}").build();
    }

    @POST
    @Path("/secured/auth/refresh-tokens")
    public Response refresh(@FormParam("refreshToken") final String refreshToken,
                            @Context final HttpServletResponse response) {
        User user = userService.findOne(securityContext.getUserPrincipal().getName());
        if (user.getRefreshTokens().remove(refreshToken)) {
            try {
                if (((Date) jwtManager.getClaims(refreshToken).get("exp")).after(new Date())) {
                    return tokensService.generateTokens(user);
                }
            } catch (ParseException e) {
                log.warning(e.toString());
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/auth/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("login") final String login,
                          @FormParam("password") final String password,
                          @Context final HttpServletResponse response) {
        log.info("Authenticating " + login);
        User user = userService.findOne(login);
        if (user != null && user.getPassword().equals(password)) {
            return tokensService.generateTokens(user);
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
