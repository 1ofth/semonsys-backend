package com.semonsys.server.service.logic.agent;

import com.semonsys.server.model.dao.Server;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.shared.RemoteCommands;
import lombok.extern.log4j.Log4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@Stateless
@Log4j
public class AgentVerifier {

    @EJB
    private ServerService serverService;

    public boolean checkAgent(final Server server) {
        if (!server.getActivated()) {

            if (!checkIpAndPort(server)) {
                return checkConnection(server);
            } else {
                return false;
            }

        } else {
            return true;
        }
    }

    private boolean checkIpAndPort(final Server server) {
        return serverService.existServerWithSameIpAndPort(server.getIp(), server.getPort());
    }

    private boolean checkConnection(final Server server) {
        try {
            Registry registry = LocateRegistry.getRegistry(server.getIp(), server.getPort());
            RemoteCommands stub = (RemoteCommands) registry.lookup("RemoteCommands");

            stub.testConnection();

            return true;
        } catch (RemoteException | NotBoundException e) {
            return false;
        }
    }
}
