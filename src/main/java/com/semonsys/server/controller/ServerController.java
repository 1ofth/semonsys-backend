package com.semonsys.server.controller;

import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.db.UserService;
import com.semonsys.server.service.logic.ServerControllerLogic;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Stateless
@Path("/rest/secured/")
public class ServerController {

    @Context
    private SecurityContext securityContext;

    @EJB
    private ServerService serverService;

    @EJB
    private UserService userService;

    @EJB
    private ServerControllerLogic logic;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getServers(@PathParam("id") final Long id) {

        return logic.getServers(securityContext.getUserPrincipal().getName(), id, serverService);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServers() {

        return logic.getServers(securityContext.getUserPrincipal().getName(), null, serverService);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addServer(@FormParam("name") final String serverName,
                              @FormParam("description") final String description,
                              @FormParam("ip") final String ip) {

        return logic.addServer(securityContext.getUserPrincipal().getName(), serverName, description, ip,
            userService, serverService);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response changeServer(@FormParam("id") final Long id,
                                 @FormParam("name") final String name,
                                 @FormParam("description") final String description,
                                 @FormParam("ip") final String ip,
                                 @FormParam("port") final String port) {

        return logic.updateServer(id, name, description, ip, port, securityContext.getUserPrincipal().getName(),
            serverService);
    }

    @DELETE
    @Path("/{id}")
    public Response removeServer(@PathParam("id") final Long id) {

        return logic.deleteServer(securityContext.getUserPrincipal().getName(), id, serverService);
    }
}
