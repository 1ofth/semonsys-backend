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
        if(find(server.getUser().getLogin(), server.getName()) == null){
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
            return null;
        }
    }

    public Server find(final String userName, final String serverName) {
        try {
            return entityManager.createQuery(
                "SELECT server " +
                    "FROM Server AS server " +
                    "WHERE server.name = :serverName AND server.user.login = :userName", Server.class)
                .setParameter("serverName", serverName)
                .setParameter("userName", userName)
                .getSingleResult();

        } catch (NoResultException e){
            return null;
        }
    }

    public List<Server> find(){
        try{
            return entityManager.createQuery("SELECT s FROM Server AS s", Server.class)
                .getResultList();
        } catch (NoResultException e){
            return new ArrayList<>();
        }
    }

    public void update(final Server server) {
        entityManager.merge(server);
    }


    public void remove(final String serverName, final String userName){
        entityManager.createQuery(
            "DELETE FROM Server AS server" +
                "WHERE server.name = :serverName AND server.user.login = :userName")
            .setParameter("serverName", serverName)
            .setParameter("userName", userName)
            .executeUpdate();
    }








    @Deprecated
    public Server find(final Long id) {
        return entityManager.find(Server.class, id);
    }

    @Deprecated
    public boolean remove(final String userName, final Long id) {
        int amount = entityManager.createQuery("delete from Server as s where s.user.login = :user AND s.id = :id")
            .setParameter("id", id)
            .setParameter("user", userName)
            .executeUpdate();

        return amount > 0;
    }

    @Deprecated
    public boolean remove(final String userName) {
        int amount = entityManager.createQuery("delete from Server as s where s.user.login = :user")
            .setParameter("user", userName)
            .executeUpdate();

        return amount > 0;
    }
}
