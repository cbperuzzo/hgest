package com.lumem.hgest.repository;

import com.lumem.hgest.model.RegisterKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterKeyRepository extends JpaRepository<RegisterKey,Long> {
    boolean existsByValueAndValid(String valeu, boolean valid);
}
