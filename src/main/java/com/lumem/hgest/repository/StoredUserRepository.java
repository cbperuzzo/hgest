package com.lumem.hgest.repository;

import com.lumem.hgest.model.user.StoredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoredUserRepository extends JpaRepository<StoredUser,Long>{
    @Query("select u from user_info u where u.userName = :alias or u.email = lower(:alias)")
    StoredUser findByAlias(@Param("alias") String alias);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

    Page<StoredUser> findByUserNameContainingIgnoreCase(String name, Pageable pageable);
}