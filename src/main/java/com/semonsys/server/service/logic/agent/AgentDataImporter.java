package com.semonsys.server.service.logic.agent;

import com.semonsys.server.model.dao.Server;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.logic.MailService;
import lombok.extern.log4j.Log4j;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
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

    @EJB
    private MailService mailService;

    @Lock(LockType.READ)
    @Schedule(second = "00", minute = "*/15", hour = "*", persistent = false)
    public void atSchedule() {
        log.info("Importing data from agents now working");

        int totalAmount = 0;
        int finished = 0;

        List<Server> servers = serverService.find();

        totalAmount = servers.size();

        for (Server server : servers) {
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
                mailService.send("Error connecting to the server with name " + serverName,
                        server.getUser().getEmail());
                log.error("Loading data from '" + serverName + "' agent failed.");
            }
        }

        log.info("Importing data from agents is finished. There was downloaded data from " + finished + "/" + totalAmount + " servers.");
    }
}
