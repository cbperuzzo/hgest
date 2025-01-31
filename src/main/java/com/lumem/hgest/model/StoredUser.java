package com.lumem.hgest.model;

import com.lumem.hgest.model.Role.RoleEnum;
import jakarta.persistence.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.random.RandomGeneratorFactory;

@Entity(name = "user_info")
public class StoredUser implements UserDetails, CredentialsContainer {
    @Id
    @GeneratedValue
    private long id;

    private String userName;

    private String salt;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private String hash;

    public StoredUser(long id, String salt, String hash, RoleEnum role, String userName) {
        this.id = id;
        this.salt = salt;
        this.hash = hash;
        this.role = role;
        this.userName = userName;
    }

    public StoredUser(String hash, RoleEnum role, String userName,String salt) {
        this.salt = salt;
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


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setHash(String password) {
        this.hash = password;
    }

    public String getHash(){
        return hash;
    }

    public String getSalt(){
        return this.salt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return getRole().getName();
            }
        });
        return list;
    }

    @Override
    public String getPassword() {
        return hash;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public void eraseCredentials() {
        this.hash = null;
        this.salt = null;
    }

    @Override
    public String toString() {
        return "StoredUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", salt='" + salt + '\'' +
                ", role=" + role +
                ", password='" + hash + '\'' +
                '}';
    }
}

