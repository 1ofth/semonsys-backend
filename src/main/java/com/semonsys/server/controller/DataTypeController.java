package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.model.dao.DataType;
import com.semonsys.server.model.dto.DataTypeTO;
import com.semonsys.server.service.db.DataTypeService;
import com.semonsys.server.service.logic.DataTypeControllerLogic;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Path("/rest/secured/data_type")
public class DataTypeController {

    @Context
    private SecurityContext securityContext;

    @EJB
    private DataTypeControllerLogic logic;

    @EJB
    private DataTypeService dataTypeService;

    @GET
    public Response getDataTypes() {
        List<DataType> list = dataTypeService.findWithDefault(securityContext.getUserPrincipal().getName());

        List<DataTypeTO> result = new ArrayList<>();
        for(DataType dataType : list){
            result.add(DataTypeTO.convert(dataType));
        }

        return Response.ok(new Gson().toJson(result)).build();
    }

    @GET
    @Path("/{id}")
    public Response getDataType(@PathParam("id") final Long id) {
        return logic.getOneDataTypeById(id);
    }

    @POST
    public Response createDataType(@FormParam("name") final String name,
                                   @FormParam("description") final String description) {
        return logic.createDataType(name, description, securityContext);
    }

    @PUT
    public Response updateDataType(@FormParam("id") final Long id,
                                   @FormParam("name") final String name,
                                   @FormParam("description") final String description) {
        return logic.updateDataType(id, name, description);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteDataType(@PathParam("id") final Long id) {
        return logic.deleteDataType(id, securityContext);
    }
}
