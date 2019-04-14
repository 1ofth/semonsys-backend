package com.semonsys.server.service.db;

import com.semonsys.server.model.Server;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ServerService {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public Server find(final String userName, final String serverName) {
        try {
            return entityManager.createQuery("select s from Server as s where s.user.login = :userLogin "
                + "and s.name LIKE :name", Server.class)
                .setParameter("userLogin", userName)
                .setParameter("name", serverName)
                .getSingleResult();

        } catch (NoResultException n) {
            return null;
        }
    }

    public List<Server> find(final String userName) {
        try {
            return entityManager.createQuery("select s from Server as s where s.user.login = :userLogin", Server.class)
                .setParameter("userLogin", userName)
                .getResultList();
        } catch (NoResultException n) {
            return null;
        }
    }

    public Server find(final Long id) {
        return entityManager.find(Server.class, id);
    }

    public void save(final Server server) {
        entityManager.persist(server);
    }

    public void update(final Server server) {
        entityManager.merge(server);
    }

    public boolean remove(final String userName, final Long id) {
        int amount = entityManager.createQuery("delete from Server as s where s.user.login = :user AND s.id = :id")
            .setParameter("id", id)
            .setParameter("user", userName)
            .executeUpdate();

        return amount > 0;
    }

    public boolean remove(final String userName) {
        int amount = entityManager.createQuery("delete from Server as s where s.user.login = :user")
            .setParameter("user", userName)
            .executeUpdate();

        return amount > 0;
    }
}
