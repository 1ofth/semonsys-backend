package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.interceptor.MethodParamsInterceptor;
import com.semonsys.server.model.dao.Server;
import com.semonsys.server.model.dto.IdentifierTO;
import com.semonsys.server.model.dto.ParamTO;
import com.semonsys.server.model.dto.SingleDataTO;
import com.semonsys.server.service.db.CompositeDataService;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.logic.agent.AgentDataGetter;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
@Path("/rest/secured/data/comp")
public class CompositeDataController {

    @EJB
    private CompositeDataService compositeDataService;

    @EJB
    private ServerService serverService;

    @EJB
    private AgentDataGetter agentDataGetter;

    @Context
    private SecurityContext securityContext;


    @GET
    @Path("/identifiers")
    @Interceptors(MethodParamsInterceptor.class)
    public Response getIdentifiers(@QueryParam("group") final String groupName,
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

        Set<String> list = compositeDataService.findIdentifiers(server.getId(), groupName);
        Set<IdentifierTO> result = new HashSet<>();

        for(String s : list){
            IdentifierTO identifier = new IdentifierTO();
            identifier.setValue(s);
            result.add(identifier);
        }

        return Response.ok(new Gson().toJson(result)).build();
    }

    @GET
    @Path("/last")
    @Interceptors(MethodParamsInterceptor.class)
    public Response getLastAll(@QueryParam("group") final String groupName,
                               @QueryParam("server") final String serverName,
                               @QueryParam("identifier") final String identifier) {
        if (groupName == null || serverName == null || identifier == null) {
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

        List<SingleDataTO> list = compositeDataService.findLastSingleDataListWithIdentifier(groupName, server.getId(), identifier);

        return Response.ok(new Gson().toJson(list)).build();
    }

    @GET
    @Path("/series")
    @Interceptors(MethodParamsInterceptor.class)
    public Response getSeries(@QueryParam("server") final String serverName,
                              @QueryParam("group") final String dataGroupName,
                              @QueryParam("type") final String dataTypeName,
                              @QueryParam("time") final Long time,
                              @QueryParam("identifier") final String identifier){

        if(serverName == null || dataGroupName == null || dataTypeName == null || time == null || identifier == null){
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

        Set<ParamTO> list = compositeDataService.findAllParamsFromTimeWithIdentifier(dataGroupName, dataTypeName, server.getId(), time, identifier);

        return Response.ok(new Gson().toJson(list)).build();
    }
}
