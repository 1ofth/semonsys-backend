package com.semonsys.server.service.db;

import com.semonsys.server.model.dao.DataType;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DataTypeServiceTest {

    private DataTypeService service;
    private EntityManager manager = mock(EntityManager.class);

    @Before
    public void setUp() {
        this.service = new DataTypeService();
        this.service.setEntityManager(manager);
    }

    @Test
    public void shouldFindByUserLogin() {
        List<DataType> expected = new ArrayList<>(Arrays.asList(
            new DataType((long) 1, "user1", "DATA", "No description"),
            new DataType((long) 2, "user1", "DATA", "No description")
        )
        );

        TypedQuery<DataType> mockedQuery = mock(TypedQuery.class);

        when(mockedQuery.setParameter(anyString(), anyString())).thenReturn(mockedQuery);
        when(mockedQuery.getResultList()).thenReturn(expected);

        when(this.manager.createQuery(anyString(), any(Class.class)))
            .thenReturn(mockedQuery);

        List<DataType> actual = service.find("user1");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSave() {
        DataType dataType = new DataType((long) 1, "user", "name", " descr");

        doNothing().when(this.manager).persist(dataType);

        service.save(dataType);

        verify(manager).persist(dataType);
    }

}
