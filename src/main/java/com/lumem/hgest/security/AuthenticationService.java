package com.lumem.hgest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public String getCurrentUserName(){
        User u =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return u.getUsername();
    }

}
