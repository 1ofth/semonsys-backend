package server.service.db;

import server.model.Server;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ServerService {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public Server find(String userName, String serverName) {
        try {
            return entityManager.createQuery("select s from Server as s where s.user.login = :userLogin " +
                "and s.name LIKE :name", Server.class)
                .setParameter("userLogin", userName)
                .setParameter("name", serverName)
                .getSingleResult();

        } catch (NoResultException n) {
            return null;
        }
    }

    public List<Server> find(String userName) {
        try {
            return entityManager.createQuery("select s from Server as s where s.user.login = :userLogin", Server.class)
                .setParameter("userLogin", userName)
                .getResultList();
        } catch (NoResultException n) {
            return null;
        }
    }

    public Server find(Long id) {
        return entityManager.find(Server.class, id);
    }

    public void save(Server server) {
        entityManager.persist(server);
    }

    public void update(Server server) {
        entityManager.merge(server);
    }

    public boolean remove(String userName, Long id) {
        int amount = entityManager.createQuery("delete from Server as s where s.user.login = :user AND s.id = :id")
            .setParameter("id", id)
            .setParameter("user", userName)
            .executeUpdate();

        return amount > 0;
    }

    public boolean remove(String userName) {
        int amount = entityManager.createQuery("delete from Server as s where s.user.login = :user")
            .setParameter("user", userName)
            .executeUpdate();

        return amount > 0;
    }
}
