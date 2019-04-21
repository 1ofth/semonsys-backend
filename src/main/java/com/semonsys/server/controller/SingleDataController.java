package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.service.db.storedData.SingleDataService;
import com.semonsys.server.model.SingleData;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.sql.Timestamp;
import java.util.List;

@Stateless
@Path("/rest/secured/data/single")
public class SingleDataController {

    @Context
    private SecurityContext securityContext;

    @EJB
    private SingleDataService singleDataService;

    @GET
    @Path("/all")
    public Response getLastAll(@QueryParam("server_id") final Long serverId) {
        if (serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<SingleData> list = singleDataService.findLastAll(serverId, securityContext.getUserPrincipal().getName());

        if (list == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new Gson().toJson(list)).build();
        }
    }

    @GET
    @Path("/one")
    public Response getLastOne(@QueryParam("data_type") final String dataType,
                               @QueryParam("server_id") final Long serverId) {
        if (dataType == null || serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        SingleData singleData = singleDataService.findLastOne(dataType, serverId);

        if (singleData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new Gson().toJson(singleData)).build();
        }
    }

    @GET
    @Path("/after")
    public Response getOneAfter(@QueryParam("data_type") final String dataType,
                                @QueryParam("server_id") final Long serverId,
                                @QueryParam("time") final Long time) {
        if (time == null || dataType == null || serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<SingleData> singleData = singleDataService.findAfter(dataType, serverId, time);

        if (singleData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new Gson().toJson(singleData)).build();
        }
    }


}
