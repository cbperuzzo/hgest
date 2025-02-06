package com.lumem.hgest.security;

import com.lumem.hgest.model.StoredUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public StoredUser getCurrentUser() {
        return (StoredUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }

    public Long getCurrentUserId() {
        StoredUser storedUser = (StoredUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return storedUser.getId();

    }
}
