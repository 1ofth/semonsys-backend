package com.semonsys.server.service.logic;

import com.semonsys.server.model.dao.DataGroup;
import com.semonsys.server.service.db.DataGroupService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

@Stateless
public class DataGroupControllerLogic {

    @EJB
    private DataGroupService dataGroupService;

    public Response getAll() {
        List<DataGroup> list = dataGroupService.find();

        if (list == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(convertToJsonArray(list)).build();
        }
    }

    public Response get(final Long id) {
        if (id == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            DataGroup dataGroup = dataGroupService.find(id);

            if (dataGroup == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok(convertToJsonObject(dataGroup)).build();
            }
        }
    }

    private JsonObject convertToJsonObject(final DataGroup group) {
        return Json.createObjectBuilder()
            .add("id", group.getId())
            .add("name", group.getName())
            .add("description", group.getDescription())
            .build();
    }

    private JsonArray convertToJsonArray(final List<DataGroup> list) {
        if (list != null) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            list.stream()
                .filter(Objects::nonNull)
                .forEach(object -> arrayBuilder.add(
                    Json.createObjectBuilder()
                        .add("id", object.getId())
                        .add("name", object.getName())
                        .add("description", object.getDescription())
                    )
                );
            return arrayBuilder.build();
        } else {
            return null;
        }
    }
}
