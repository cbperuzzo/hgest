package com.lumem.hgest.repository;

import com.lumem.hgest.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenId(String tokenId);

    @Modifying
    @Query("delete from RefreshToken r where r.expiresAt < :threshold")
    void deleteAllExpiredBefore(@Param("threshold") Instant threshold);

    @Modifying
    @Query("""
       update RefreshToken r
       set r.revoked = true,
           r.revokedAt = :now
       where r.user.id = :userId
         and r.revoked = false
         and r.expiresAt > :now
       """)
    void revokeAllActiveByUser(Long userId, Instant now);

}

