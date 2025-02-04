package com.lumem.hgest.security;

import com.lumem.hgest.model.StoredUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public StoredUser getCurrentUser(){
        return (StoredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }

    public StoredUser getCurrent(){
        return (StoredUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

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
