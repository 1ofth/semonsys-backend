package com.semonsys.server.controller;

import com.semonsys.server.model.dao.User;
import com.semonsys.server.service.db.UserService;
import com.semonsys.server.service.logic.RegistrationService;
import lombok.extern.java.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;

@Stateless
@Path(PathHolder.REGISTRATION_PATH)
@Log
public class RegistrationController {
    private static final int CREDENTIALS_MAX_LENGTH = 15;
    @Context
    private SecurityContext securityContext;

    @Inject
    private RegistrationService registrationService;

    @Inject
    private UserService userService;

    @POST
    @Path(PathHolder.REGISTRATION_ENDPOINT_PATH)
    public Response registerNewUser(@FormParam("login") final String login,
                                    @FormParam("password") final String password,
                                    @FormParam("email") final String email,
                                    @Context final HttpServletRequest request) {
        if (validateCredentials(login, password) && userService.find(login) == null) {
            User user = new User(login, password, email, new ArrayList<>(), false, null);
            userService.save(user);
            String appUrl = request.getRequestURL().toString().replace("/registration", "");
            registrationService.sendConfirmationMessage(user, appUrl);
            return Response.status(Response.Status.CREATED)
                    .entity("{user: '" + login + "'}")
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private boolean validateCredentials(final String login, final String password) {
        if (login == null || login.equals("") || login.length() > CREDENTIALS_MAX_LENGTH) {
            return false;
        }
        return password != null && !password.equals("") && password.length() <= CREDENTIALS_MAX_LENGTH;
    }

    @POST
    @Path(PathHolder.ACTIVATE_ACCOUNT_PATH)
    public Response sendConfirmationEmail(@Context final HttpServletRequest request) {
        User user = userService.find(securityContext.getUserPrincipal().getName());
        if (user.getEmail() == null || user.getVerified()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String appUrl = request.getRequestURL().toString().replace("/secured/activate", "");
        registrationService.sendConfirmationMessage(user, appUrl);
        return Response.ok().build();
    }

    @GET
    @Path(PathHolder.CONFIRM_REGISTRATION_PATH + "/{token}")
    public Response confirmRegistration(@PathParam("token") final String token) {
        User user = userService.findUserByToken(token);
        if (user == null || !user.getVerificationToken().equals(token)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        user.setVerificationToken(null);
        user.setVerified(true);
        userService.update(user);
        return Response.ok("{message: 'Illuminati confirmed'}").build();
    }
}
