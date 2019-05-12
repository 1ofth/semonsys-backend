package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.interceptor.MethodParamsInterceptor;
import com.semonsys.server.model.dao.Server;
import com.semonsys.server.model.dto.ParamTO;
import com.semonsys.server.model.dto.SingleDataTO;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.db.SingleDataService;
import com.semonsys.server.service.logic.agent.AgentDataGetter;
import lombok.extern.log4j.Log4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

@Log4j
@Stateless
@Path("/rest/secured/data/sing")
public class SingleDataController {

    @EJB
    private SingleDataService singleDataService;

    @EJB
    private ServerService serverService;

    @EJB
    private AgentDataGetter agentDataGetter;


    @Context
    private SecurityContext securityContext;


    @GET
    @Path("/last")
    @Interceptors(MethodParamsInterceptor.class)
    public Response getLastAll(@QueryParam("group") final String groupName,
                               @QueryParam("server") final String serverName){
        if(groupName == null || serverName == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Server server = serverService.find(securityContext.getUserPrincipal().getName(), serverName);

        if(server == null || !server.getActivated()){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            agentDataGetter.updateData(server);
        } catch (RemoteException | NotBoundException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        Set<SingleDataTO> result = singleDataService.findLastSingleDataPack(groupName, server.getId());

        return Response.ok(new Gson().toJson(result)).build();
    }

    @GET
    @Path("/series")
    @Interceptors(MethodParamsInterceptor.class)
    public Response getSeries(@QueryParam("server") final String serverName,
                              @QueryParam("group") final String dataGroupName,
                              @QueryParam("type") final String dataTypeName,
                              @QueryParam("time") final Long time){

        if(serverName == null || dataGroupName == null || dataTypeName == null || time == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Server server = serverService.find(securityContext.getUserPrincipal().getName(), serverName);

        if(server == null || !server.getActivated()){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        log.info("Data getting for agent '" + serverName + "' is being processing.");

        try {
            agentDataGetter.updateData(server);
        } catch (RemoteException | NotBoundException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        Set<ParamTO> result = singleDataService.findAllParamsFromTime(dataGroupName, dataTypeName, server.getId(), time);

        return Response.ok(new Gson().toJson(result)).build();
    }


}
