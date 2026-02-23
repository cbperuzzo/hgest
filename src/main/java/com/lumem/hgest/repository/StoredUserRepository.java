package com.lumem.hgest.repository;

import com.lumem.hgest.model.user.StoredUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredUserRepository extends JpaRepository<StoredUser,Long>{
    StoredUser getByUserName(String userName);
    boolean existsByUserName(String userName);
}