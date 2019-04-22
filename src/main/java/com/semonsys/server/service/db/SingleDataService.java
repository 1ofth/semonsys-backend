package com.semonsys.server.service.db;

import com.semonsys.server.model.dao.SingleData;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SingleDataService {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public SingleData find(final long id) {
        return entityManager.find(SingleData.class, id);
    }

    public List<SingleData> find() {
        try {
            return entityManager.createQuery("SELECT data FROM SingleData AS data", SingleData.class)
                .getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }
    
    public List<SingleData> findAfter(final long time){
        try {
            return entityManager.createQuery("SELECT data FROM SingleData AS data WHERE data.time > :time",
                SingleData.class)
                .setParameter("time", time)
                .getResultList();

        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }
}
