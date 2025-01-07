package com.lumem.hgest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity(name = "user_info")
public class StoredUser implements UserDetails, CredentialsContainer {
    @Id
    @GeneratedValue
    private long id;

    private String userName;

    private String salt;

    private String role;

    private String password;

    public StoredUser(long id, String salt, String password, String role, String userName) {
        this.id = id;
        this.salt = salt;
        this.password = password;
        this.role = role;
        this.userName = userName;
    }

    public StoredUser(String salt, String password, String role, String userName) {
        this.salt = salt;
        this.password = password;
        this.role = role;
        this.userName = userName;
    }

    public StoredUser() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt(){
        return this.salt;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
        this.salt = null;
    }
}
