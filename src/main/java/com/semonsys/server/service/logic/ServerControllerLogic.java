package com.semonsys.server.service.logic;

import com.semonsys.server.model.Server;
import com.semonsys.server.model.User;
import com.semonsys.server.service.db.ServerService;
import com.semonsys.server.service.db.UserService;
import lombok.extern.java.Log;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log
@Stateless
public class ServerControllerLogic {

    // finds out all servers of given user. If name of server is given then it returns only that server but in list too
    public Response getServers(final String userName,
                               final Long id, final ServerService serverService) {
        List<Server> list = new ArrayList<>();
        if (id != null) {
            Server server = serverService.find(id);
            if (server.getUser().getLogin().equals(userName)) {
                list.add(server);
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } else {
            list = serverService.find(userName);
        }

        if (list != null) {
            JsonArray jsonArray = convertListToJson(list);
            return Response.ok(jsonArray).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("User " + userName + " doesn't have any servers").build();
        }
    }

    // adds one server to user list. Uses default values for port, activated, activation data
    public Response addServer(final String userName, final String serverName,
                              final String description, final String ip,
                              final UserService userService, final ServerService serverService) {
        User user = userService.find(userName);
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("User with name " + userName + " was not found").build();
        }

        if (ip == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Ip of server is needed").build();
        }

        if (serverName == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Name of server is needed").build();
        }

        Server server = new Server(user, serverName, description, ip);

        serverService.save(server);

        return Response.ok().build();
    }

    public Response updateServer(final Long id, final String name,
                                 final String description, final String ip,
                                 final String port, final String userName,
                                 final ServerService serverService) {
        log.info("Trying to update server with income data:\n\tid: " + id + "\n\tname: " + name + "\n\tdescr: "
            + description + "\n\tip: " + ip + "\n\tport: " + port + "\n");

        if (id != null) {
            Server server = serverService.find(id);

            if (server == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Server with name " + name + " was not found").build();
            }

            if (!server.getUser().getLogin().equals(userName)) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User must be an owner of a server").build();
            }

            if (ip != null) {
                server.setIp(ip);
            }

            if (port != null) {
                server.setPort(Integer.parseInt(port));
            }

            if (name != null) {
                server.setName(name);
            }

            if (description != null) {
                server.setDescription(description);
            }

            serverService.update(server);

            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("Id of given server is needed").build();
        }
    }

    public Response deleteServer(final String userName, final Long id,
                                 final ServerService serverService) {
        if (id == null) {
            serverService.remove(userName);
        } else {
            serverService.remove(userName, id);
        }

        return Response.ok().build();
    }

    private JsonArray convertListToJson(final List<Server> list) {
        if (list != null) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            list.stream()
                .filter(Objects::nonNull)
                .forEach(server -> arrayBuilder.add(
                    Json.createObjectBuilder()
                        .add("id", server.getId())
                        .add("name", server.getName())
                        .add("user", server.getUser().getLogin())
                        .add("description", server.getDescription())
                        .add("ip", server.getIp())
                        .add("port", server.getPort())
                        .add("activated", server.getActivated())
                    )
                );
            return arrayBuilder.build();
        } else {
            return null;
        }
    }
}
