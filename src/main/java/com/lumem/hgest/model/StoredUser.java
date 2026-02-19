package com.lumem.hgest.model;

import com.lumem.hgest.model.Role.RoleEnum;
import jakarta.persistence.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


@Entity(name = "user_info")
public class StoredUser {
    @Id
    @GeneratedValue
    private long id;

    private String userName;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private String hash;

    public StoredUser(long id, String hash, RoleEnum role, String userName) {
        this.id = id;
        this.hash = hash;
        this.role = role;
        this.userName = userName;
    }

    public StoredUser(String hash, RoleEnum role, String userName) {
        this.hash = hash;
        this.role = role;
        this.userName = userName;
    }

    public StoredUser() {
    }

    public RoleEnum getRole(){
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setHash(String password) {
        this.hash = password;
    }

    public String getHash(){
        return hash;
    }


}

