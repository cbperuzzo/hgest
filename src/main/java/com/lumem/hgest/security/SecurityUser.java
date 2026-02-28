package com.lumem.hgest.security;

import com.lumem.hgest.model.role.RoleEnum;
import com.lumem.hgest.model.user.StoredUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private final long id;
    private final String userName;
    private final RoleEnum role;
    private final String password;

    public SecurityUser(StoredUser user) {

        this.id = user.getId();
        this.userName = user.getUserName();
        this.role = user.getRole();
        this.password = user.getHash();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_"+role.toString().toUpperCase())
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public long getId() {
        return id;
    }

    public RoleEnum getRole() {
        return role;
    }
}

