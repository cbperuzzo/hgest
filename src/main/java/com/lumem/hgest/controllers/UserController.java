package com.lumem.hgest.controllers;

import com.lumem.hgest.model.role.RoleEnum;
import com.lumem.hgest.model.user.StoredUser;
import com.lumem.hgest.model.user.UserDTO;
import com.lumem.hgest.repository.RefreshTokenRepository;
import com.lumem.hgest.repository.ShiftRepository;
import com.lumem.hgest.repository.StoredUserRepository;
import com.lumem.hgest.security.SecurityUser;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("api/user")
@Transactional
public class UserController {

    PasswordEncoder passwordEncoder;
    StoredUserRepository storedUserRepository;
    RefreshTokenRepository refreshTokenRepository;

    public UserController(PasswordEncoder passwordEncoder, StoredUserRepository storedUserRepository, RefreshTokenRepository refreshTokenRepository) {
        this.passwordEncoder = passwordEncoder;
        this.storedUserRepository = storedUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
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
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateRequest request){

        RoleEnum actorRole = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole();

        if (!storedUserRepository.existsById(id))
            return ResponseEntity.status(400).body("no such user");

        StoredUser target = storedUserRepository.getReferenceById(id);

        if (!actorRole.hasControlOver(target.getRole()))
            return ResponseEntity.status(400).body("the current user doesn't have authority over the target user");

        RoleEnum newRole = RoleEnum.getRoleByName(request.role());

        if (newRole != null && !actorRole.hasControlOver(newRole))
            return ResponseEntity.status(400).body("the current user doesn't have the authority to assign this role to other users");

        if (newRole != null)
            target.setRole(newRole);

        if (request.name() != null  && !request.name().isEmpty())
            target.setUserName(request.name());

        if (request.password() != null  && !request.password().isEmpty())
            target.setHash(passwordEncoder.encode(request.password()));

        storedUserRepository.save(target);

        return ResponseEntity.ok().build();

    }
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    @PatchMapping("disable/{id}")
    public ResponseEntity<?> disable(@PathVariable("id") Long id){
        RoleEnum actorRole = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole();

        if (!storedUserRepository.existsById(id))
            return ResponseEntity.status(400).body("no such user");

        StoredUser target = storedUserRepository.getReferenceById(id);

        if (!actorRole.hasControlOver(target.getRole()))
            return ResponseEntity.status(400).body("the current user doesn't have authority over the target user");

        target.setActive(false);

        refreshTokenRepository.revokeAllActiveByUser(target.getId(), Instant.now());

        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public Page<UserDTO> search(
            @RequestParam(required = false) String name,
            Pageable pageable){

        Page<StoredUser> page;

        if (name == null || name.isBlank()) {
            page = storedUserRepository.findAll(pageable);
        } else {
            page = storedUserRepository
                    .findByUserNameContainingIgnoreCase(name, pageable);
        }

        return page.map(UserDTO::new);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id){

        return storedUserRepository.findById(id)
                .map(user -> ResponseEntity.ok(new UserDTO(user)))
                .orElse(ResponseEntity.status(404).build());
    }

    public record UpdateRequest(@Nullable String name,@Nullable String password,@Nullable String role) {}
    public record CreateRequest(@NotNull String name,@NotNull String password,@NotNull String role) {}
}
