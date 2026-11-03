package com.lumem.hgest.security;


import com.lumem.hgest.model.user.StoredUser;
import com.lumem.hgest.repository.StoredUserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class HGestUserDetailsService implements UserDetailsService {

    private StoredUserRepository source;

    public HGestUserDetailsService(StoredUserRepository source) {
        this.source = source;
    }

    @Override
    public SecurityUser loadUserByUsername(String alias) throws UsernameNotFoundException {
        StoredUser u = source.findByAlias(alias);

        if (u == null || !u.isActive()){
            throw new UsernameNotFoundException("username not found");
        }

        return new SecurityUser(u);

    }
}
