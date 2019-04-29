package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.model.dao.Server;
import com.semonsys.server.model.dao.User;
import com.semonsys.server.model.dto.ServerTO;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.db.UserService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Path("/rest/secured/server")
public class ServerController {

    @Context
    private SecurityContext securityContext;

    @EJB
    private ServerService serverService;

    @EJB
    private UserService userService;

    // Returns a list of all servers current user has
    @GET
    public Response getServers() {
        List<ServerTO> result = new ArrayList<>();
        List<Server> list = serverService.find(securityContext.getUserPrincipal().getName());

        int i = 1;
        if(list != null) {
            for (Server server : list) {
                ServerTO serverTO = new ServerTO();

                serverTO.setIp(server.getIp());
                serverTO.setId(i);
                serverTO.setPort(server.getPort());
                serverTO.setName(server.getName());
                serverTO.setDescription(server.getDescription());
                serverTO.setActivated(server.getActivated());

                result.add(serverTO);

                i+=1;
            }
        }

        return Response.ok(new Gson().toJson(result)).build();
    }


    // saves a new server if there is no server with given name for current user
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response addServer(@FormParam("name") final String serverName,
                              @FormParam("description") final String description,
                              @FormParam("ip") final String ip) {
        if(serverName == null || description == null || ip == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        User user = userService.find(securityContext.getUserPrincipal().getName());
        if(user == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Server server = new Server();

        server.setDescription(description);
        server.setIp(ip);
        server.setName(serverName);
        server.setUser(user);

        if(serverService.save(server)){
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

        if(name == null || (description == null && ip == null && port == null)){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Server server = serverService.find(securityContext.getUserPrincipal().getName(), name);
        if(server == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(description != null){
            server.setDescription(description);
        }

        if(ip != null){
            server.setIp(ip);
        }

        if(port != null){
            server.setPort(Integer.parseInt(port));
        }

        serverService.update(server);

        return Response.ok().build();
    }

    // TODO don't know why bat it is always bad request
    // deletes given server
    @DELETE
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response removeServer(@FormParam("name") final String name) {
        if(name == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        serverService.remove(name, securityContext.getUserPrincipal().getName());

        return Response.ok().build();
    }
}
