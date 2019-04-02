package server.controller;

import server.service.db.ServerService;
import server.service.db.UserService;
import server.service.logic.ServerControllerLogic;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Stateless
@Path("/rest/secured/server")
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
    public Response getServers(@PathParam("id") Long id) {

        return logic.getServers(securityContext.getUserPrincipal().getName(), id, serverService);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServers() {

        return logic.getServers(securityContext.getUserPrincipal().getName(), null, serverService);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addServer(@FormParam("name") String serverName,
                              @FormParam("description") String description,
                              @FormParam("ip") String ip) {

        return logic.addServer(securityContext.getUserPrincipal().getName(), serverName, description, ip,
            userService, serverService);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response changeServer(@FormParam("id") Long id,
                                 @FormParam("name") String name,
                                 @FormParam("description") String description,
                                 @FormParam("ip") String ip,
                                 @FormParam("port") String port) {

        return logic.updateServer(id, name, description, ip, port, securityContext.getUserPrincipal().getName(),
            serverService);
    }

    @DELETE
    @Path("/{id}")
    public Response removeServer(@PathParam("id") Long id) {

        return logic.deleteServer(securityContext.getUserPrincipal().getName(), id, serverService);
    }
}
