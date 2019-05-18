package com.semonsys.server.service.db;

import com.semonsys.server.model.dao.DataGroup;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class DataGroupService {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public List<DataGroup> find() {
        try {
            return entityManager.createQuery("SELECT dg FROM DataGroup AS dg", DataGroup.class)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public DataGroup find(final Long id) {
        return entityManager.find(DataGroup.class, id);
    }

    public DataGroup find(final String name) {
        try {
            return entityManager.createQuery("SELECT dg FROM DataGroup AS dg WHERE dg.name = :name", DataGroup.class)
                .setParameter("name", name)
                .getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }


    public void save(final DataGroup dataGroup) {
        entityManager.persist(dataGroup);
    }

    public void update(final DataGroup dataGroup) {
        entityManager.merge(dataGroup);
    }
}
