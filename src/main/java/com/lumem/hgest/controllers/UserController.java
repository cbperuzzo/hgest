package com.lumem.hgest.controllers;

import com.lumem.hgest.model.role.RoleEnum;
import com.lumem.hgest.model.user.StoredUser;
import com.lumem.hgest.repository.StoredUserRepository;
import com.lumem.hgest.security.SecurityUser;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Transactional
public class UserController {

    PasswordEncoder passwordEncoder;
    StoredUserRepository storedUserRepository;

    public UserController(PasswordEncoder passwordEncoder, StoredUserRepository storedUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.storedUserRepository = storedUserRepository;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateRequest request){

        RoleEnum newRole = RoleEnum.getRoleByName(request.role());
        RoleEnum creatorRole = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole();

        if (newRole == null){
            return ResponseEntity.status(400).body("no such role found");
        }
        if (!creatorRole.hasControlOver(newRole)){
            return ResponseEntity.status(403).body("the current user doesn't have the necessary authority to create new users with the selected role");
        }
        if (request.name().isEmpty() || request.password().isEmpty()){
            return ResponseEntity.status(400).body("required fields are missing or are empty");
        }
        if (storedUserRepository.existsByUserName(request.name())){
            return ResponseEntity.status(400).body("a user with this exact name is already in the database");
        }

        StoredUser newUser = new StoredUser();
        newUser.setUserName(request.name());
        newUser.setHash(passwordEncoder.encode(request.password()));
        newUser.setRole(newRole);
        newUser.setActive(true);

        storedUserRepository.save(newUser);

        return ResponseEntity.ok().build();

    }
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> update(){
        //update user (name password and role)
        // supervisor -> workers
        // admin -> workers, supervisors
        // dev -> all
        return null;
        //TODO
    }
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> disable(){
        //mark as disabled
        //remove all refresh tokens
        return null;
        //TODO
    }

    // get list of user by name LIKE
    // get user by ID

    public record UpdateRequest(@Nullable String name, @Nullable String password) {}
    public record CreateRequest(@NotNull String name, @NotNull String password,@NotNull String role) {}
}
