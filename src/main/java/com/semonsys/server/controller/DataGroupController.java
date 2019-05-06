package com.semonsys.server.controller;

import com.semonsys.server.service.logic.DataGroupControllerLogic;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
@Stateless
@Path("/rest/secured/data_group")
public class DataGroupController {

    @EJB
    private DataGroupControllerLogic logic;

    @GET
    public Response getAll() {
        return logic.getAll();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") final Long id) {
        return logic.get(id);
    }
}
