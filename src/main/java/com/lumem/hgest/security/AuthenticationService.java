package com.lumem.hgest.security;

import org.springframework.security.core.context.SecurityContextHolder;
import com.lumem.hgest.model.StoredUser;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public StoredUser getCurrentUser(){
        return (StoredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
