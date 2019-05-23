package com.semonsys.server.controller;

import com.semonsys.server.interceptor.MethodParamsInterceptor;
import com.semonsys.server.model.dao.User;
import com.semonsys.server.security.Encoder;
import com.semonsys.server.security.JwtManager;
import com.semonsys.server.service.db.UserService;
import com.semonsys.server.service.logic.TokensService;
import lombok.extern.java.Log;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
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
import java.util.Map;

@Stateless
@Path(PathHolder.AUTH_PATH)
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
    @Path(PathHolder.LOGOUT_PATH)
    public Response logout() {
        tokensService.clearRefreshTokens(securityContext.getUserPrincipal().getName());
        return Response.ok("{message: 'logged out'}").build();
    }

    @POST
    @Path(PathHolder.REFRESH_TOKENS_PATH)
    public Response refresh(@FormParam("refreshToken") final String refreshToken) {
        try {
            Map<String, Object> claims = jwtManager.getClaims(refreshToken);
            if (((Date) claims.get("exp")).after(new Date())) {
                User user = userService.find((String) claims.get("sub"));
                if (user.getRefreshTokens().remove(refreshToken)) {
                    return tokensService.generateTokens(user);
                }
            }
        } catch (ParseException e) {
            log.warning(e.toString());
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Expired or invalid token").build();
    }

    @Interceptors(MethodParamsInterceptor.class)
    @POST
    @Path(PathHolder.LOGIN_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("login") final String login,
                          @FormParam("password") final String password) {
        log.info("Authenticating " + login);
        User user = userService.find(login);
        if (user != null && Encoder.validatePassword(password, user.getPassword())) {
            return tokensService.generateTokens(user);
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
