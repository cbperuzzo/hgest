package com.lumem.hgest.security;


import com.lumem.hgest.model.User;
import com.lumem.hgest.repository.UserInfoRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class HGestUserDetailsService implements UserDetailsService {

    private UserInfoRepository source;

    public HGestUserDetailsService(UserInfoRepository source) {
        this.source = source;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = source.getByUserName(username);

        if (u == null){
            throw new UsernameNotFoundException("username not found");
        }

        return new org.springframework.security.core.userdetails.User(u.getUserName(),u.getPassword(),true, true,true,
                true, List.of(new SimpleGrantedAuthority("USER")));

    }
}
