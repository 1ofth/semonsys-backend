package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.model.dao.SingleData;
import com.semonsys.server.model.dto.SingleDataTO;
import com.semonsys.server.service.db.storedData.SingleDataServiceN;
import com.semonsys.server.model.dao.SingleDataN;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Log4j
@Stateless
@Path("/rest/secured/data/single")
public class SingleDataController {
    @Context
    private SecurityContext securityContext;

    @EJB
    private SingleDataServiceN singleDataService;

    @EJB
    private com.semonsys.server.service.db.SingleDataService singleDataServiceNN;

    @GET
    @Path("/test")
    public Response test(){
        List<SingleData> singleData = singleDataServiceNN.find().subList(0, 10);
        List<SingleDataTO> singleDataTOES = new ArrayList<>();

        for(SingleData data : singleData){
            SingleDataTO singleDataTO = new SingleDataTO();

            log.debug("SingleData object: " + data.getParam().getLongValue() + "  " + data.getParam().getDoubleValue() +
                "  " + data.getParam().getStringValue() + "  " + data.getTime());

            singleDataTO.setTime(data.getTime());

            if(data.getParam().getDoubleValue() != null) {
                singleDataTO.setValue(data.getParam().getDoubleValue().toString());
                log.debug("double\n");
            } else if(data.getParam().getLongValue() != null){
                singleDataTO.setValue(data.getParam().getLongValue().toString());
                log.debug("long\n");
            } else {
                singleDataTO.setValue(data.getParam().getStringValue());
                log.debug("string\n");
            }

            singleDataTOES.add(singleDataTO);
        }

        return Response.ok(new Gson().toJson(singleDataTOES)).build();
    }

    @GET
    @Path("/all")
    public Response getLastAll(@QueryParam("server_id") final Long serverId) {
        if (serverId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<SingleDataN> list = singleDataService.findLastAll(serverId, securityContext.getUserPrincipal().getName());

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

        SingleDataN singleData = singleDataService.findLastOne(dataType, serverId);

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

        List<SingleDataN> singleData = singleDataService.findAfter(dataType, serverId, time);

        if (singleData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(new Gson().toJson(singleData)).build();
        }
    }


}
