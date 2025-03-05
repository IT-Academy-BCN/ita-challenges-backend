package com.itachallenge.auth.dto;


public class User {

    private String uuid;

    private String username;

    private String role;

    public User(String uuid, String username, String role) {
        this.uuid = uuid;
        this.username = username;
        this.role = role;
    }

    public User() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
