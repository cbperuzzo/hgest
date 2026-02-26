package com.lumem.hgest.model.user;

import com.lumem.hgest.model.role.RoleEnum;
import com.lumem.hgest.security.SecurityUser;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public record UserDTO (
        long id,
        boolean active,
        String userName,
        String role
) {
    public UserDTO(StoredUser storedUser){
        this(storedUser.getId(),storedUser.isActive(), storedUser.getUserName(),storedUser.getRole().getName());
    }

}
