package com.semonsys.server.service.db;

import com.semonsys.server.model.dao.Server;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ServerService {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;


    // saves server only when there is no server with the same name for given user
    public boolean save(final Server server) {
        if (find(server.getUser().getLogin(), server.getName()) == null) {
            entityManager.persist(server);
            return true;
        }

        return false;
    }


    public List<Server> find(final String userName) {
        try {
            return entityManager.createQuery("select s from Server as s where s.user.login = :userLogin", Server.class)
                .setParameter("userLogin", userName)
                .getResultList();
        } catch (NoResultException n) {
            return new ArrayList<>();
        }
    }

    public List<Server> findActivated(final String userName) {
        try {
            return entityManager.createQuery(
                "select s "
                    + "from Server as s "
                    + "where "
                    + "   s.user.login = :userLogin"
                    + "   AND s.activated = true", Server.class)
                .setParameter("userLogin", userName)
                .getResultList();
        } catch (NoResultException n) {
            return new ArrayList<>();
        }
    }

    public boolean existServerWithSameIpAndPort(final String ip, final int port) {
        try {
            Server server = entityManager.createQuery(
                "SELECT "
                    + "   s "
                    + "FROM "
                    + "   Server AS s "
                    + "WHERE "
                    + "   s.ip = :ip "
                    + "   AND s.port = :port "
                    + "   AND s.activated = true ",
                Server.class
            )
                .setParameter("ip", ip)
                .setParameter("port", port)
                .getSingleResult();

            if (server != null) {
                return true;
            }

        } catch (NoResultException e) {
            return false;
        }

        return false;
    }

    public Server find(final String userName, final String serverName) {
        try {
            return entityManager.createQuery(
                "SELECT server "
                    + "FROM Server AS server "
                    + "WHERE server.name = :serverName AND server.user.login = :userName", Server.class)
                .setParameter("serverName", serverName)
                .setParameter("userName", userName)
                .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Server> find() {
        try {
            return entityManager.createQuery("SELECT s FROM Server AS s", Server.class)
                .getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public void update(final Server server) {
        entityManager.merge(server);
    }


    public void remove(final String serverName, final String userName) {
        Server server = find(userName, serverName);

        if(server == null){
            return;
        }

        entityManager.remove(server);
    }


    @Deprecated
    public Server find(final Long id) {
        return entityManager.find(Server.class, id);
    }
}
