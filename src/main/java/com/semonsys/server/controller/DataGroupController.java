package com.semonsys.server.controller;

import com.google.gson.Gson;
import com.semonsys.server.model.dao.DataGroup;
import com.semonsys.server.model.dto.DataGroupTO;
import com.semonsys.server.service.db.DataGroupService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Path("/rest/secured/group")
public class DataGroupController {

    @EJB
    private DataGroupService dataGroupService;

    // returns all data groups objects
    @GET
    public Response get() {
        List<DataGroupTO> result = new ArrayList<>();
        List<DataGroup> list = dataGroupService.find();

        for (DataGroup dataGroup : list) {
            DataGroupTO dataGroupTO = new DataGroupTO();

            dataGroupTO.setDescription(dataGroup.getDescription());
            dataGroupTO.setName(dataGroup.getName());

            result.add(dataGroupTO);
        }

        return Response.ok(new Gson().toJson(result)).build();
    }
}
