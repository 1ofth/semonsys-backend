package com.semonsys.server.service.db;

import com.semonsys.server.model.dao.User;
import com.semonsys.server.security.Encoder;
import lombok.Setter;

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

    public void save(final User user) {
        user.setPassword(Encoder.generatePasswordHash(user.getPassword()));
        entityManager.persist(user);
    }

    public void update(final User user) {
        entityManager.merge(user);
    }

    public List<User> getAll() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    public User findOne(final String login) {
        return entityManager.find(User.class, login);
    }

    public User find(final String login) {
        return entityManager.find(User.class, login);
    }

    public User findUserByToken(final String token) {
        try {
            return entityManager.createQuery("select u from User u where u.verificationToken = :token", User.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException n) {
            return null;
        }
    }
}
