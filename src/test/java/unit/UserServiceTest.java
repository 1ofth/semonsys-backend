package unit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import server.model.User;
import server.service.db.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private EntityManager entityManager = mock(EntityManager.class);

    @Before
    public void setUp() {
        this.userService = new UserService();
        this.userService.setEntityManager(entityManager);
    }

    @Test
    public void shouldGetUserList() {
        List<User> expected = new ArrayList<>(Arrays.asList(
                new User("test1", "test1"),
                new User("test2", "test2")));

        TypedQuery<User> mockedQuery = mock(TypedQuery.class);
        when(mockedQuery.getResultList()).thenReturn(expected);
        when(this.entityManager.createQuery(anyString(), any(Class.class)))
                .thenReturn(mockedQuery);

        List<User> actual = this.userService.getAll();

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSave() {
        User user = new User("test", "test");
        doNothing().when(this.entityManager).persist(user);
        this.userService.save(user);
        verify(this.entityManager).persist(user);
    }

    @Test
    public void shouldGetUserByLogin() {
        User expected = new User("123", "345");
        when(this.entityManager.find(ArgumentMatchers.any(), ArgumentMatchers.anyString()))
                .thenReturn(expected);
        User actual = this.userService.findOne("123");
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNull() {
        when(this.entityManager.find(ArgumentMatchers.any(), ArgumentMatchers.anyString()))
                .thenReturn(null);
        User actual = this.userService.findOne("123");
        assertNull(actual);
    }
}
