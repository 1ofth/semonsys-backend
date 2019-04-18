package com.semonsys.server.service.logic.agent;

import com.semonsys.server.model.Server;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.db.storedData.CompositeDataService;
import com.semonsys.server.service.db.storedData.SingleDataService;
import com.semonsys.shared.CompositeData;
import com.semonsys.shared.RemoteCommands;
import com.semonsys.shared.SingleData;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

    public List<CompositeData> getData(final long serverId) throws RemoteException, NotBoundException {
        Server server = serverService.find(serverId);

        if (server == null) {
            return null;
        }

        long time1 = singleDataService.getMaxTime();
        long time2 = compositeDataService.getMaxTime();

        if (time1 > time2) {
            time1 = time2;
        }

        return getData(server, time1);
    }

    public List<CompositeData> getData(final Server server, final long timeFrom) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
        RemoteCommands stub = (RemoteCommands) registry.lookup("RemoteCommands");

        List<CompositeData> list = stub.getData(timeFrom);

        Long time = list.get(list.size() - 1).getData().get(0).getTime().getTime();

        stub.removeData(time);

        for (CompositeData compositeData : list) {
            if (compositeData.getName().equals("SingleData")) {
                List<SingleData> singleData = compositeData.getData();

                for (SingleData singleData1 : singleData) {
                    singleDataService.save(singleData1, server.getId());
                }

            } else {
                compositeDataService.save(compositeData, server.getId());
            }
        }

        return list;
    }

}
