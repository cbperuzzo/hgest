package com.lumem.hgest.bootstrap;

import com.lumem.hgest.model.role.RoleEnum;
import com.lumem.hgest.model.user.StoredUser;
import com.lumem.hgest.repository.StoredUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialUserSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InitialUserSeeder.class);

    private final StoredUserRepository storedUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${hgest.bootstrap.username:}")
    private String username;
    @Value("${hgest.bootstrap.password:}")
    private String password;

    public InitialUserSeeder(StoredUserRepository storedUserRepository, PasswordEncoder passwordEncoder) {
        this.storedUserRepository = storedUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (storedUserRepository.count() > 0)
            return;

        if (username.isBlank() || password.isBlank()) {
            log.warn("the database has no users and hgest.bootstrap.username/password are not set, nobody will be able to log in");
            return;
        }

        StoredUser user = new StoredUser();
        user.setUserName(username);
        user.setHash(passwordEncoder.encode(password));
        user.setRole(RoleEnum.DEV);
        user.setActive(true);

        storedUserRepository.save(user);

        log.info("created initial DEV user '{}'", username);
    }
}
