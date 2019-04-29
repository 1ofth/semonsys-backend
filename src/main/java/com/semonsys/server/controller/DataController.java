package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.model.dao.*;
import com.semonsys.server.model.dto.CompositeDataTO;
import com.semonsys.server.model.dto.ParamTO;
import com.semonsys.server.model.dto.SingleDataTO;
import com.semonsys.server.service.db.*;
import com.semonsys.server.service.logic.agent.AgentDataGetter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Path("/rest/secured/data")
public class DataController {

    @EJB
    private SingleDataService singleDataService;

    @EJB
    private ServerService serverService;

    @EJB
    private CompositeDataService compositeDataService;

    @EJB
    private AgentDataGetter agentDataGetter;

    @EJB
    private DataTypeService dataTypeService;

    @EJB
    private DataGroupService dataGroupService;


    @Context
    private SecurityContext securityContext;


    @GET
    @Path("/test")
    public Response test(){
        CompositeData compositeData = new CompositeData();

        compositeData.setIdentifier("Some identifier");

        List<SingleData> singleData = new ArrayList<>();

        DataType dataType = dataTypeService.findByName("Название");
        DataGroup dataGroup = dataGroupService.find("OS");
        Server server = serverService.find(securityContext.getUserPrincipal().getName(), "Server 3");

        for(int i = 0; i < 10; i++){
            SingleData data = new SingleData();

            Param param = new Param();
            param.setDoubleValue( 1.5D * i);

            data.setTime(System.currentTimeMillis());
            data.setParam(param);

            data.setDataType(dataType);

            data.setDataGroup(dataGroup);

            data.setServer(server);

            data.setCompositeData(compositeData);

            singleData.add(data);
        }

        compositeData.setServer(server);
        compositeData.setData(singleData);

        compositeDataService.save(compositeData);

        return Response.ok().build();
    }









}
