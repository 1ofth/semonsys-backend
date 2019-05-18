package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.model.dao.Server;
import com.semonsys.server.model.dao.User;
import com.semonsys.server.model.dto.ServerTO;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.db.UserService;
import com.semonsys.server.service.logic.agent.AgentVerifier;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Path(PathHolder.SERVER_PATH)
public class ServerController {

    private static final Integer DEFAULT_PORT = 10_000;

    @Context
    private SecurityContext securityContext;

    @EJB
    private ServerService serverService;

    @EJB
    private UserService userService;

    @EJB
    private AgentVerifier agentVerifier;


    // Returns a list of all servers current user has
    @GET
    public Response getServers() {
        List<ServerTO> result = new ArrayList<>();
        List<Server> list = serverService.find(securityContext.getUserPrincipal().getName());

        for (Server server : list) {
            result.add(ServerTO.convert(server));
        }

        return Response.ok(new Gson().toJson(result)).build();
    }

    @GET
    @Path(PathHolder.SERVER_ACTIVATED_PATH)
    public Response getActivatedServers() {
        List<ServerTO> result = new ArrayList<>();
        List<Server> list = serverService.findActivated(securityContext.getUserPrincipal().getName());

        for (Server server : list) {
            result.add(ServerTO.convert(server));
        }

        return Response.ok(new Gson().toJson(result)).build();
    }

    @GET
    @Path(PathHolder.SERVER_ACTIVATION_PATH)
    public Response activateServer(@QueryParam("name") final String serverName) {
        if (serverName == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Parameters are incorrect").build();
        }

        Server server = serverService.find(securityContext.getUserPrincipal().getName(), serverName);

        if (server == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (agentVerifier.checkAgent(server)) {
            server.setActivated(true);
            serverService.update(server);
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Agent was already created").build();
        }
    }

    // saves a new server if there is no server with given name for current user
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addServer(@FormParam("name") final String serverName,
                              @FormParam("description") final String description,
                              @FormParam("port") final String port,
                              @FormParam("ip") final String ip) {
        if (serverName == null || description == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Parameters are incorrect").build();
        }

        User user = userService.find(securityContext.getUserPrincipal().getName());
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Server server = new Server();

        server.setDescription(description);
        server.setName(serverName);
        server.setUser(user);

        if (ip != null) {
            server.setIp(ip);
        }

        if (port != null) {
            try {
                server.setPort(Integer.parseInt(port));
            } catch (NumberFormatException ignore) {
                server.setPort(DEFAULT_PORT);
            }
        }

        if (serverService.save(server)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }


    // update any data of server
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response changeServer(@FormParam("name") final String name,
                                 @FormParam("description") final String description,
                                 @FormParam("ip") final String ip,
                                 @FormParam("port") final String port) {

        if (name == null || description == null && ip == null && port == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Parameters are incorrect").build();
        }

        Server server = serverService.find(securityContext.getUserPrincipal().getName(), name);
        if (server == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (description != null) {
            server.setDescription(description);
        }

        if (ip != null) {
            server.setActivated(false);
            server.setIp(ip);
        }

        if (port != null) {
            server.setActivated(false);
            server.setPort(Integer.parseInt(port));
        }

        serverService.update(server);

        return Response.ok().build();
    }

    // deletes given server
    @DELETE
    public Response removeServer(@QueryParam("name") final String serverName) {
        if (serverName == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("name is null!").build();
        }

        serverService.remove(serverName, securityContext.getUserPrincipal().getName());

        return Response.ok().build();
    }
}
