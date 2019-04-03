package server.controller;

import lombok.extern.java.Log;
import server.model.User;
import server.service.db.UserService;
import server.service.logic.MailService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;

@Stateless
@Path("/rest")
@Log
public class RegistrationController {
    @Context
    private SecurityContext securityContext;

    @Inject
    private MailService mailService;

    @Inject
    private UserService userService;

    @POST
    @Path("/registration")
    public Response registerNewUser(@FormParam("login") String login,
                                    @FormParam("password") String password,
                                    @FormParam("email") String email,
                                    @Context HttpServletRequest request) {
        if (validateCredentials(login, password) && userService.find(login) == null) {
            User user = new User(login, password, email, new ArrayList<>(), false, null);
            userService.save(user);
            String appUrl = request.getRequestURL().toString().replace("/registration", "");
            mailService.sendTo(user, appUrl);
            return Response.status(Response.Status.CREATED)
                    .entity("{user: '" + login + "'}")
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private boolean validateCredentials(String login, String password) {
        if (login == null || login.equals("") || login.length() > 15) {
            return false;
        }
        return password != null && !password.equals("") && password.length() <= 15;
    }

    @POST
    @Path("/secured/activate")
    public Response sendConfirmationEmail(@Context HttpServletRequest request) {
        User user = userService.find(securityContext.getUserPrincipal().getName());
        if (user.getEmail() == null || user.getVerified()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String appUrl = request.getRequestURL().toString().replace("/secured/activate", "");
        mailService.sendTo(user, appUrl);
        return Response.ok().build();
    }

    @GET
    @Path("/confirm/{token}")
    public Response confirmRegistration(@PathParam("token") String token) {
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