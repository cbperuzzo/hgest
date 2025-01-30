package com.lumem.hgest.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.lumem.hgest.model.StoredUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class AuthenticationService {

    public StoredUser getCurrentUser(){
        return (StoredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }



//    public String execTest(){
//
//        //Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//        //return authorities.stream().toList().getFirst().getAuthority().toString();
//
//        StoredUser user =  (StoredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return user.toString();
//    }

}
