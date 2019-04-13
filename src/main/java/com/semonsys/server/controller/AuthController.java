package com.semonsys.server.controller;

import lombok.extern.java.Log;
import com.semonsys.server.model.User;
import com.semonsys.server.security.JwtManager;
import com.semonsys.server.service.db.UserService;
import com.semonsys.server.service.logic.TokensService;

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
    @Path("/secured/logout")
    public Response logout() {
        tokensService.clearRefreshTokens(securityContext.getUserPrincipal().getName());
        return Response.ok("{message: 'logged out'}").build();
    }

    @POST
    @Path("/secured/refresh-tokens")
    public Response refresh(@FormParam("refreshToken") String refreshToken,
                            @Context HttpServletResponse response) {
        User user = userService.find(securityContext.getUserPrincipal().getName());
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
        User user = userService.find(login);
        if (user != null && user.getPassword().equals(password)) {
            return tokensService.generateTokens(user);
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
