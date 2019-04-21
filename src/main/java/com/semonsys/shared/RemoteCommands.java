package com.semonsys.shared;

import com.semonsys.server.model.CompositeData;
import com.semonsys.server.model.SingleData;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteCommands extends Remote {
    String testConnection() throws RemoteException;

    List<AgentSingleData> getData(Long timeFrom) throws RemoteException;

    void removeData(Long time) throws RemoteException;
}
