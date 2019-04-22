package com.semonsys.server.service.logic.agent;

import com.semonsys.server.model.dao.CompositeDataN;
import com.semonsys.server.model.dao.Server;
import com.semonsys.server.model.dao.SingleDataN;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.db.storedData.CompositeDataServiceN;
import com.semonsys.server.service.db.storedData.SingleDataServiceN;
import com.semonsys.shared.AgentSingleData;
import com.semonsys.shared.DataType;
import com.semonsys.shared.RemoteCommands;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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
public class AgentDataGetter {

    @EJB
    private SingleDataServiceN singleDataService;

    @EJB
    private CompositeDataServiceN compositeDataService;

    @EJB
    private ServerService serverService;

    public String test(final long serverId) throws RemoteException, NotBoundException, MalformedURLException {
        Server server = serverService.find(serverId);

        if (server == null) {
            return null;
        }

        Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
        RemoteCommands stub = (RemoteCommands) registry.lookup("RemoteCommands");

        return stub.testConnection();
    }

    public void updateData(final long serverId) throws RemoteException, NotBoundException {
        Server server = serverService.find(serverId);

        if (server == null) {
            return;
        }

        long time1 = singleDataService.getMaxTime();
        long time2 = compositeDataService.getMaxTime();

        if (time1 > time2) {
            time1 = time2;
        }

        updateData(server, time1);


    }

    private void updateData(final Server server, final long timeFrom) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
        RemoteCommands stub = (RemoteCommands) registry.lookup("RemoteCommands");

        List<AgentSingleData> dataFromAgent = stub.getData(timeFrom);

        // there may be some data which would be stored as composite data (field "compositeDataIdentifier")
        List<AgentSingleData> singleData = new ArrayList<>();
        List<AgentSingleData> compositeData = new ArrayList<>();

        for(AgentSingleData data : dataFromAgent){
            if(data.getCompositeDataIdentifier() != null){
                compositeData.add(data);
            } else {
                singleData.add(data);
            }
        }

        dataFromAgent = null;

        List<SingleDataN> transformedSingleData = transformToSingleData(singleData);

        singleDataService.save(transformedSingleData, server.getId());

        long maxTime = transformedSingleData.get(transformedSingleData.size()-1).getTime();

        stub.removeData(maxTime);
    }


    private Map<String, List<AgentSingleData>> partitionAgentDataList(final List<AgentSingleData> dataList){

        Map<String, List<AgentSingleData>> result = new HashMap<>();

        for(AgentSingleData data : dataList){
            if(result.containsKey(data.getCompositeDataIdentifier())){
                //result.get()
            }
        }

        return null;
    }

    // this method should get a list of AgentSingleData which have equal CompositeDataIdentifier's
    private List<CompositeDataN> transformToCompositeData(final List<AgentSingleData> dataList){
        List<CompositeDataN> result = new ArrayList<>();

        // time value of given agent single data in milliseconds. would be updated later
        long time = 0;

        List<SingleDataN> tempList = null;
        CompositeDataN tempCompositeDataObject = null;

        for(AgentSingleData data : dataList){

            // if a time difference is greater than 1 second then it was probably a new composite data object
            // => need to create new instances and save old ones
            if(data.getTime() - time > 1000){
                time = data.getTime();

                if(tempList != null){
                    tempCompositeDataObject.setData(tempList);
                }
                if(tempCompositeDataObject != null){
                    result.add(tempCompositeDataObject);
                }

                tempCompositeDataObject = new CompositeDataN();
                tempCompositeDataObject.setName(data.getCompositeDataIdentifier());

                tempList = new ArrayList<>();
            }

            // fill single data list
            if (tempList != null) {
                tempList.add(transformToSingleData(data));
            }
        }

        return result;
    }


    private List<SingleDataN> transformToSingleData(final List<AgentSingleData> dataList){
        List<SingleDataN> resultList = new ArrayList<>();

        for(AgentSingleData data : dataList){
            resultList.add(transformToSingleData(data));
        }

        return resultList;
    }


    private SingleDataN transformToSingleData(final AgentSingleData data){
        SingleDataN singleData = new SingleDataN();

        singleData.setDataTypeName(data.getDataTypeName());
        singleData.setGroupName(data.getGroupName());
        singleData.setTime(data.getTime());
        if(data.getType() == DataType.LONG) {
            singleData.setValue((Long)data.getValue());
        } else if(data.getType() == DataType.DOUBLE) {
            singleData.setValue((Double) data.getValue());
        } else if(data.getType() == DataType.STRING) {
            singleData.setValue((String) data.getValue());
        }

        return singleData;
    }
}
