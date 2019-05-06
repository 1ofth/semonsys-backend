package com.semonsys.server.service.db;

import com.semonsys.server.model.DataType;
import lombok.Setter;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class DataTypeService {

    @Setter
    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public List<DataType> find(final String userLogin) {
        try {
            return entityManager.createQuery("SELECT dt FROM DataType AS dt WHERE dt.user_login = :login", DataType.class)
                .setParameter("login", userLogin)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    // returns all types. And user's, and default ones
    public List<DataType> findWithDefault(final String userName) {
        try {
            return entityManager.createQuery("SELECT dt FROM DataType AS dt WHERE dt.user_login is null "
                + "OR dt.user_login = :login", DataType.class)
                .setParameter("login", userName)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public DataType find(final Long id) {
        return entityManager.find(DataType.class, id);
    }

    public DataType findByName(final String typeName) {
        return entityManager.createQuery("SELECT dt FROM DataType AS dt WHERE dt.name = :name", DataType.class)
            .setParameter("name", typeName)
            .getSingleResult();
    }

    public void save(final DataType dataType) {
        entityManager.persist(dataType);
    }

    public void update(final DataType dataType) {
        entityManager.merge(dataType);
    }

    public void remove(final Long id) {
        entityManager.createQuery("DELETE FROM DataType AS dt WHERE dt.id = :id")
            .setParameter("id", id)
            .executeUpdate();
    }

    // removes in case dataType's owner is given user
    public boolean removeUserType(final Long id, final String userName) {
        int amount = entityManager.createQuery("DELETE FROM DataType AS dt WHERE dt.user_login = :login AND dt.id = :id")
            .setParameter("id", id)
            .setParameter("login", userName)
            .executeUpdate();

        return amount > 0;
    }
}
