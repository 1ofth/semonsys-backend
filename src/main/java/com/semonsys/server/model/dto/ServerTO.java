package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;
import com.semonsys.server.model.dao.Server;

public class ServerTO {
    @SerializedName(value = "n")
    private String name = "";
    @SerializedName(value = "d")
    private String description = "";
    @SerializedName(value = "i")
    private String ip = "";
    @SerializedName(value = "p")
    private int port = -1;
    @SerializedName(value = "a")
    private boolean activated = false;

    public ServerTO() {
    }

    public static ServerTO convert(final Server server) {
        ServerTO serverTO = new ServerTO();

        serverTO.setActivated(server.getActivated());
        serverTO.setDescription(server.getDescription());
        serverTO.setName(server.getName());
        serverTO.setPort(server.getPort());
        serverTO.setIp(server.getIp());

        return serverTO;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(final boolean activated) {
        this.activated = activated;
    }
}
