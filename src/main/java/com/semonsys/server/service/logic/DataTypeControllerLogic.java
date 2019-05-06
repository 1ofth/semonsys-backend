package com.semonsys.server.service.logic;

import com.semonsys.server.model.DataType;
import com.semonsys.server.service.db.DataTypeService;
import lombok.Setter;
import lombok.extern.java.Log;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Objects;

@Log
@Stateless
public class DataTypeControllerLogic {

    @Setter
    @EJB
    private DataTypeService dataTypeService;

    public Response getAllTypes(final String login) {

        log.info("Getting all data type objects by owner with name = " + login);

        List<DataType> list = dataTypeService.findWithDefault(login);

        if (list != null) {
            JsonArray jsonArray = convertToJsonArray(list);
            if (jsonArray != null) {
                return Response.ok(jsonArray).build();
            } else {
                log.severe("A problem while converting DataType object to JsonArray was found");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            log.severe("No DataType objects were found for user " + login);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Response getOneDataTypeById(final Long id) {

        log.info("Getting one data type object by id = " + id);

        DataType dataType = dataTypeService.find(id);

        if (dataType != null) {
            JsonObject jsonObject = convertToJsonObject(dataType);
            if (jsonObject != null) {
                return Response.ok(jsonObject).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    public Response createDataType(final String name, final String descr, final SecurityContext securityContext) {

        log.info("Creating new data type with params:\n\tname: " + name + "\n\tdescription: " + descr);

        if (name == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        DataType dataType = new DataType();

        dataType.setName(name);

        if (descr != null) {
            dataType.setDescription(descr);
        }

        dataType.setUserLogin(securityContext.getUserPrincipal().getName());

        dataTypeService.save(dataType);

        return Response.ok().build();
    }

    public Response updateDataType(final Long id, final String name, final String description) {

        log.info("Updating data type with params:\n\tid:" + id + "\n\tname:" + name + "\n\tdescription" + description);

        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (name == null && description == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        DataType dataType = dataTypeService.find(id);

        if (dataType == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (name != null) {
            dataType.setName(name);
        }

        if (description != null) {
            dataType.setDescription(description);
        }

        dataTypeService.update(dataType);

        return Response.ok().build();
    }

    public Response deleteDataType(final Long id, final SecurityContext securityContext) {

        log.info("Deleting data type with id = " + id);

        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (dataTypeService.removeUserType(id, securityContext.getUserPrincipal().getName())) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private JsonObject convertToJsonObject(final DataType type) {
        if (type != null) {
            return Json.createObjectBuilder()
                .add("id", type.getId())
                .add("name", type.getName())
                .add("description", type.getDescription())
                .add("user_login", type.getUserLogin())
                .build();
        } else {
            return null;
        }
    }

    private JsonArray convertToJsonArray(final List<DataType> list) {
        if (list != null) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            list.stream()
                .filter(Objects::nonNull)
                .forEach(object -> arrayBuilder.add(
                    Json.createObjectBuilder()
                        .add("id", object.getId())
                        .add("user_login", object.getUserLogin())
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
