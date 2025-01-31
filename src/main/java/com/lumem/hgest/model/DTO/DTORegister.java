package com.lumem.hgest.model.DTO;

public class DTORegister{
    private String username;
    private String password;
    private String cpassword;
    private String key;

    public DTORegister(String key, String password, String passwordConfirmation, String username) {
        this.key = key;
        this.password = password;
        this.cpassword = passwordConfirmation;
        this.username = username;
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

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String Cpassword) {
        this.cpassword = Cpassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean doPasswordsMatch(){
        return password.equals(cpassword);
    }


}
