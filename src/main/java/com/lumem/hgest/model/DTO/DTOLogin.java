package com.lumem.hgest.model.DTO;

public class DTOLogin {
    private String username;
    private String password;

    public DTOLogin(String password, String username) {
        this.password = password;
        this.username = username;
    }

    public DTOLogin() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
