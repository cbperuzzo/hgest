package com.lumem.hgest.model.DTO;

public class DTORegister{
    private String Username;
    private String password;
    private String passwordConfirmation;
    private String key;

    public DTORegister(String key, String password, String passwordConfirmation, String username) {
        this.key = key;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        Username = username;
    }

    public DTORegister(String key) {
        this.key = key;
    }

    public DTORegister() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
