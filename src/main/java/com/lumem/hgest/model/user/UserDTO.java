package com.lumem.hgest.model.user;

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
