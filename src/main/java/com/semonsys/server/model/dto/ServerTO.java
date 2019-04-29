package com.semonsys.server.model.dto;

public class ServerTO {
    private long id = 0;
    private String n = "";
    private String d = "";
    private String i = "";
    private int p = -1;
    private boolean a = false;

    public ServerTO() {}

    public String getName() {
        return n;
    }

    public void setName(String name) {
        this.n = name;
    }

    public String getDescription() {
        return d;
    }

    public void setDescription(String description) {
        this.d = description;
    }

    public String getIp() {
        return i;
    }

    public void setIp(String ip) {
        this.i = ip;
    }

    public int getPort() {
        return p;
    }

    public void setPort(int port) {
        this.p = port;
    }

    public boolean isActivated() {
        return a;
    }

    public void setActivated(boolean activated) {
        this.a = activated;
    }
}