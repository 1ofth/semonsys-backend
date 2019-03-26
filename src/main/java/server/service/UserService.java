package server.service;

import server.model.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UserService {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public UserService(){}

    public void saveUser(User user){
        entityManager.persist(user);
  }

    public List<User> getAll() {
        return (List<User>) entityManager.createQuery("select u from User u").getResultList();
    }
    public User findOne(String login) {
        User user;
        try {
             user = (User) entityManager.createQuery(" select u from User u where u.login = :login")
                    .setParameter("login", login).getSingleResult();
        } catch (NoResultException e ) {
            System.out.println("no user with such login");
            return null;
        }
        return user;
    }

}
