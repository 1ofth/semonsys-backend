package server.service.db;

import lombok.Setter;
import server.model.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UserService {

    @Setter
    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public void saveUser(final User user) {
        entityManager.persist(user);
    }

    public List<User> getAll() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    public User findOne(final String login) {
        return entityManager.find(User.class, login);
    }

}
