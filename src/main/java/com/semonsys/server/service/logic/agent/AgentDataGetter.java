package com.semonsys.server.service.logic.agent;

import com.semonsys.server.model.Server;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.db.storedData.CompositeDataService;
import com.semonsys.server.service.db.storedData.SingleDataService;
import com.semonsys.server.model.CompositeData;
import com.semonsys.shared.AgentSingleData;
import com.semonsys.shared.DataType;
import com.semonsys.shared.RemoteCommands;
import com.semonsys.server.model.SingleData;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AgentDataGetter {

    @EJB
    private SingleDataService singleDataService;

    @EJB
    private CompositeDataService compositeDataService;

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

        List<SingleData> transformedSingleData = transformToSingleData(singleData);

        singleDataService.save(transformedSingleData, server.getId());
        long maxTime = transformedSingleData.get(transformedSingleData.size()-1).getTime();

        stub.removeData(maxTime);
    }

    private List<SingleData> transformToSingleData(final List<AgentSingleData> dataList){
        List<SingleData> resultList = new ArrayList<>();

        for(AgentSingleData data : dataList){
            SingleData singleData = new SingleData();

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

            resultList.add(singleData);
        }

        return resultList;
    }
}
