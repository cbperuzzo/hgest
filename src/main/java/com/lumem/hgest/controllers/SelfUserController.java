package com.lumem.hgest.controllers;

import com.lumem.hgest.model.user.StoredUser;
import com.lumem.hgest.security.SecurityUser;
import com.lumem.hgest.repository.StoredUserRepository;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional
@RequestMapping("/self/user")
public class SelfUserController {

    StoredUserRepository storedUserRepository;
    PasswordEncoder passwordEncoder;

    public SelfUserController(StoredUserRepository storedUserRepository, PasswordEncoder passwordEncoder) {
        this.storedUserRepository = storedUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateSelf(@RequestBody UpdateSelfRequest request){

        long id = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        StoredUser user = storedUserRepository.getReferenceById(id);

        if (!passwordEncoder.matches(request.currentPassword(),user.getHash())){
            return ResponseEntity.status(401).build();
        }

        boolean dirty = false;
        if (request.name() != null && !request.name().isEmpty()) {
            user.setUserName(request.name());
            dirty = true;
        }
        if (request.newPassword() != null && !request.newPassword().isEmpty()){
            user.setHash(passwordEncoder.encode(request.newPassword()));
            dirty = true;
        }
        if (dirty)
            storedUserRepository.save(user);

        return ResponseEntity.ok().build();
    }

    //get self

    public record UpdateSelfRequest(@Nullable String name, @Nullable String newPassword, String currentPassword) {}

}
