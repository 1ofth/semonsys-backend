package com.semonsys.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteCommands extends Remote {
    String testConnection() throws RemoteException;

    List<AgentSingleData> getData(Long timeFrom) throws RemoteException;

    void removeData(Long time) throws RemoteException;
}
