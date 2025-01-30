package com.lumem.hgest.model;

import com.lumem.hgest.model.Role.RoleEnum;
import jakarta.persistence.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "user_info")
public class StoredUser implements UserDetails, CredentialsContainer {
    @Id
    @GeneratedValue
    private long id;

    private String userName;

    private String salt;

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    private String password;

    public StoredUser(long id, String salt, String password, RoleEnum role, String userName) {
        this.id = id;
        this.salt = salt;
        this.password = password;
        this.role = role;
        this.userName = userName;
    }

    public StoredUser(String salt, String password, RoleEnum role, String userName) {
        this.salt = salt;
        this.password = password;
        this.role = role;
        this.userName = userName;
    }

    public StoredUser() {
    }

    public String getRole() {
        return role.getName();
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



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return getRole();
            }
        });
        return list;
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

    @Override
    public String toString() {
        return "StoredUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", salt='" + salt + '\'' +
                ", role=" + role +
                ", password='" + password + '\'' +
                '}';
    }
}
