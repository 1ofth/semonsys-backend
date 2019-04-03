package server.service.db;

import lombok.Setter;
import server.model.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UserService {

    @Setter
    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public void save(User user) {
        entityManager.persist(user);
    }

    public void update(User user) {
        entityManager.merge(user);
    }

    public List<User> getAll() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    public User find(String login) {
        return entityManager.find(User.class, login);
    }

    public User findUserByToken(String token) {
        try {
            return entityManager.createQuery("select u from User u where u.verificationToken = :token", User.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException n) {
            return null;
        }
    }
}
