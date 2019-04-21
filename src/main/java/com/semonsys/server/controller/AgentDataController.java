package com.semonsys.server.controller;

import com.semonsys.server.service.logic.agent.AgentDataGetter;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


@Path("/rest/secured/agent")
public class AgentDataController {

    @EJB
    private AgentDataGetter agentDataGetter;

    @GET
    @Path("/test")
    public Response test(@QueryParam("server_id") final Long serverId) {
        if (serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            String string = agentDataGetter.test(serverId);

            if (string == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("NULL result").build();
            } else {
                return Response.ok(string).build();
            }

        } catch (RemoteException | MalformedURLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } catch (NotBoundException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("NotBoundException").build();
        }
    }

    @GET
    @Path("/update")
    public Response updateData(@QueryParam("server_id") final Long serverId) {
        if (serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            agentDataGetter.updateData(serverId);
        } catch (RemoteException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Problems with RMI connection\n" + e.getMessage()).build();
        } catch (NotBoundException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("NotBoundException").build();
        }

        return Response.ok().build();

        // add parsing those data and saving it to db
    }
}
