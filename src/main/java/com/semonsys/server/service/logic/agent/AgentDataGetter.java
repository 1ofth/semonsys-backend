package com.semonsys.server.service.logic.agent;

import com.semonsys.server.interceptor.MethodParamsInterceptor;
import com.semonsys.server.model.dao.*;
import com.semonsys.server.service.db.*;
import com.semonsys.shared.AgentSingleData;
import com.semonsys.shared.RemoteCommands;
import lombok.extern.log4j.Log4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@Log4j
public class AgentDataGetter {

    @EJB
    private SingleDataService singleDataService;

    @EJB
    private CompositeDataService compositeDataService;

    @EJB
    private ServerService serverService;

    @EJB
    private DataTypeService dataTypeService;

    @EJB
    private DataGroupService dataGroupService;


    public String test(final long serverId) throws RemoteException, NotBoundException, MalformedURLException {
        Server server = serverService.find(serverId);

        if (server == null) {
            return null;
        }

        Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
        RemoteCommands stub = (RemoteCommands) registry.lookup("RemoteCommands");

        return stub.testConnection();
    }

    @Interceptors(MethodParamsInterceptor.class)
    public void updateData(final Server server) throws RemoteException, NotBoundException {
        long time1 = singleDataService.getMaxTime();
        long time2 = compositeDataService.getMaxTime();

        if (time1 > time2) {
            time1 = time2;
        }

        log.info("Loading data for agent '" + server.getName() + "' is being processed with last time=" + time1 + ".");

        updateData(server, time1);
    }


    private void updateData(final Server server, final long timeFrom) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
        RemoteCommands stub = (RemoteCommands) registry.lookup("RemoteCommands");

        List<AgentSingleData> dataFromAgent = stub.getData(timeFrom);

        log.info("Loaded " + dataFromAgent.size() + " rows from '" + server.getName() + "' agent.");

        // there may be some data which would be stored as composite data (field "compositeDataIdentifier")
        List<AgentSingleData> singleData = new ArrayList<>();
        List<AgentSingleData> compositeData = new ArrayList<>();

        long maxTime = 0;

        for(AgentSingleData data : dataFromAgent){
            if(data.getTime() > maxTime){
                maxTime = data.getTime();
            }

            if(data.getCompositeDataIdentifier() != null){
                compositeData.add(data);
            } else {
                singleData.add(data);
            }
        }

        dataFromAgent = null;



        List<SingleData> singleDataList = convertToSingleData(singleData, server);
        singleDataService.save(singleDataList);

        log.info(singleDataList.size() + " SingleData objects were saved to db");



        List<CompositeData> compositeDataList = convertToCompositeData(compositeData, server);
        compositeDataService.save(compositeDataList);

        log.info(compositeDataList.size() + " CompositeData objects were saved to db");


        stub.removeData(maxTime);

        log.info("Data from agent was removed");

    }



    private SingleData convertToSingleData(final AgentSingleData data, final Server server){
        SingleData singleData = new SingleData();

        DataType dataType = dataTypeService.findByName(data.getDataTypeName());
        if(dataType == null){
            return null;
        }

        DataGroup dataGroup = dataGroupService.find(data.getGroupName());
        if (dataGroup == null){
            return null;
        }

        singleData.setServer(server);
        singleData.setDataGroup(dataGroup);
        singleData.setDataType(dataType);
        singleData.setTime(data.getTime());

        Param param = new Param();
        if(data.getType() == com.semonsys.shared.DataType.STRING){
            param.setStringValue( (String) data.getValue());
        } else if(data.getType() == com.semonsys.shared.DataType.LONG){
            param.setLongValue( (Long) data.getValue());
        } else if(data.getType() == com.semonsys.shared.DataType.DOUBLE){
            param.setDoubleValue( (Double)data.getValue() );
        } else {
            return null;
        }

        singleData.setParam(param);

        return singleData;
    }

    private List<SingleData> convertToSingleData(final List<AgentSingleData> data, final Server server){
        List<SingleData> list = new ArrayList<>();

        for(AgentSingleData temp : data){
            list.add(convertToSingleData(temp, server));
        }

        return list;
    }

    private List<CompositeData> convertToCompositeData(final List<AgentSingleData> list, final Server server){
        Map<String, List<AgentSingleData>> filteredData = new HashMap<>();

        // sort data by identifier
        for(AgentSingleData data : list){
            if(filteredData.containsKey(data.getCompositeDataIdentifier())){
                filteredData.get(data.getCompositeDataIdentifier()).add(data);
            } else {
                List<AgentSingleData> temp = new ArrayList<>();
                temp.add(data);
                filteredData.put(data.getCompositeDataIdentifier(), temp);
            }
        }

        List<CompositeData> result = new ArrayList<>();

        filteredData.forEach((key, value) -> {
            long lastTime = 0;
            final long TIME_DIFFERENCE = 1000;

            CompositeData tempCompositeData = null;
            List<SingleData> tempSingleDataList = null;

            for (AgentSingleData data : value) {

                if(lastTime == 0){
                    lastTime = data.getTime();
                }

                if (tempCompositeData == null) {
                    tempCompositeData = new CompositeData();
                    tempCompositeData.setServer(server);
                    tempCompositeData.setIdentifier(key);

                    tempSingleDataList = new ArrayList<>();
                }

                // if it is a new composite data object
                if (data.getTime() - lastTime >= TIME_DIFFERENCE) {
                    tempCompositeData.setData(tempSingleDataList);

                    result.add(tempCompositeData);

                    tempCompositeData = null;
                    tempSingleDataList = new ArrayList<>();
                }

                SingleData singleData = convertToSingleData(data, server);
                if(singleData != null) {
                    singleData.setCompositeData(tempCompositeData);
                    tempSingleDataList.add(singleData);
                }
                lastTime = data.getTime();
            }
        });

        return result;
    }

}
