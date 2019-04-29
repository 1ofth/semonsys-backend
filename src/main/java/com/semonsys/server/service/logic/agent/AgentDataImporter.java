package com.semonsys.server.service.logic.agent;

import com.semonsys.server.model.dao.Server;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.logic.agent.AgentDataGetter;
import lombok.extern.log4j.Log4j;

import javax.ejb.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

@Singleton
@Log4j
public class AgentDataImporter {

    @EJB
    private ServerService serverService;

    @EJB
    private AgentDataGetter agentDataGetter;


    @Lock(LockType.READ)
    @Schedule(second = "00", minute = "*/15", hour = "*", persistent = false)
    public void atSchedule() {
        log.info("Importing data from agents now working");

        int totalAmount = 0;
        int finished = 0;

        List<Server> servers = serverService.find();

        totalAmount = servers.size();

        for(Server server : servers){
            String userName;
            String serverName = "none";
            try {
                serverName = server.getName();
                userName = server.getUser().getLogin();

                log.info("Current pair: '" + serverName + "' : '" + userName + "'.");
                agentDataGetter.updateData(server);
                log.info("Successfully loaded.");

                finished += 1;
            } catch (RemoteException | NotBoundException ignore) {
                log.error("Loading data from '" + serverName + "' agent failed.");
            }
        }

        log.info("Importing data from agents is finished. There was downloaded data from " + finished + "/" + totalAmount + " servers.");
    }
}
