package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;

public class ServerTO {
    private long id = 0;
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

    public ServerTO() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}