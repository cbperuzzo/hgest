package com.lumem.hgest.security;


import com.lumem.hgest.model.StoredUser;
import com.lumem.hgest.repository.StoredUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class HGestUserDetailsService implements UserDetailsService {

    private StoredUserRepository source;

    public HGestUserDetailsService(StoredUserRepository source) {
        this.source = source;
    }

    @Override
    public StoredUser loadUserByUsername(String username) throws UsernameNotFoundException {
        StoredUser u = source.getByUserName(username);

        if (u == null){
            throw new UsernameNotFoundException("username not found");
        }

        return u;

    }
}
