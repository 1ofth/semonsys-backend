package com.semonsys.server.service.logic;

import lombok.extern.log4j.Log4j;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;

@Singleton
@Log4j
public class DataBaseCleaner {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    @Schedule(second = "00", minute = "00", hour = "22", persistent = false)
    public void deleteOldData(){
        long time = System.currentTimeMillis() - 60*60*24*3;
        log.info("Starting deleting old data after time=" + time);



        entityManager.createNativeQuery(
            "DELETE FROM data WHERE time > :time ")
            .setParameter("time", time)
            .executeUpdate();



        log.info("Deleting old data finished");
    }
}
