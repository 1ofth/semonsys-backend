package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.service.db.storedData.CompositeDataService;
import com.semonsys.server.model.CompositeData;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.List;

@Stateless
@Path("/rest/secured/data/composite")
public class CompositeDataController {

    @EJB
    private CompositeDataService compositeDataService;

    @GET
    @Path("/one")
    public Response getLastOne(@QueryParam("identifier") final String identifier,
                               @QueryParam("server_id") final Long serverId) {
        if (identifier == null || serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        CompositeData compositeData = compositeDataService.findLastOne(identifier, serverId);

        if (compositeData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new Gson().toJson(compositeData)).build();
        }
    }

    @GET
    @Path("/all")
    public Response getLastAll(@QueryParam("server_id") final Long serverId) {
        if (serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<CompositeData> compositeData = compositeDataService.findLastAll(serverId);

        return Response.ok(new Gson().toJson(compositeData)).build();
    }

    @GET
    @Path("/after")
    public Response getOneAfter(@QueryParam("identifier") final String identifier,
                                @QueryParam("server_id") final Long serverId,
                                @QueryParam("time") final Long time) {
        if (identifier == null || serverId == null || time == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<CompositeData> list = compositeDataService.findOneAfter(identifier, serverId, new Timestamp(time));

        return Response.ok(new Gson().toJson(list)).build();
    }

    @GET
    @Path("/identifiers")
    public Response getIdentifiers(@QueryParam("server_id") final Long serverId) {
        if (serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<String> list = compositeDataService.findIdentifiers(serverId);

        if (list == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new Gson().toJson(list)).build();
        }
    }

}
