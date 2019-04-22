package com.semonsys.server.service.db;

import com.semonsys.server.model.dao.CompositeData;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CompositeDataService {
    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public CompositeData find(final long id) {return entityManager.find(CompositeData.class, id);}

    // returns all last composite data objects
    public List<CompositeData> find() {
        try {
            return entityManager.createQuery(
                 "SELECT data " +
                    "FROM CompositeData AS data " +
                    "GROUP BY data.identifier" +
                    "HAVING MAX(data.id)", CompositeData.class)
                .getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }
}
