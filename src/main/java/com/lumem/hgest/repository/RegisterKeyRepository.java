package com.lumem.hgest.repository;

import com.lumem.hgest.model.RegisterKey;
import com.lumem.hgest.model.StoredUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterKeyRepository extends JpaRepository<RegisterKey,String> {
    boolean existsByValueAndValid(String valeu, boolean valid);
}
