package com.lumem.hgest.model.Util;

import com.lumem.hgest.model.Role.RoleEnum;
import com.lumem.hgest.model.StoredUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private final StoredUser storedUser;

    private final long id;
    private final String userName;
    private final RoleEnum role;
    private final String password;

    public SecurityUser(StoredUser user) {

        storedUser = user;

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

    public StoredUser getStoredUser() {
        return storedUser;
    }

    public RoleEnum getRole() {
        return role;
    }
}

