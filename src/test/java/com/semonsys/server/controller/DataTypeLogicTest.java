package com.semonsys.server.controller;

import com.semonsys.server.service.db.DataTypeService;
import com.semonsys.server.service.logic.DataTypeControllerLogic;
import org.junit.Before;

import javax.ws.rs.core.SecurityContext;

import static org.mockito.Mockito.mock;

public class DataTypeLogicTest {

    private DataTypeControllerLogic logic;
    private SecurityContext securityContext = mock(SecurityContext.class);
    private DataTypeService dataTypeService = mock(DataTypeService.class);

    @Before
    public void setUp() {
        logic = new DataTypeControllerLogic();
        logic.setDataTypeService(dataTypeService);
    }

    // TODO


}
