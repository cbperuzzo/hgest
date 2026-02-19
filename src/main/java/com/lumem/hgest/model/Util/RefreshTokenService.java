package com.lumem.hgest.model.Util;

import com.lumem.hgest.model.RefreshToken;
import com.lumem.hgest.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final RefreshTokenCrypto crypto;

    public RefreshTokenService(RefreshTokenRepository repository,
                               RefreshTokenCrypto crypto) {
        this.repository = repository;
        this.crypto = crypto;
    }

    public String create(SecurityUser user) {

        String tokenId = UUID.randomUUID().toString();
        byte[] secretBytes = crypto.generateSecret();

        String secretEncoded = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(secretBytes);

        String hash = crypto.hash(secretBytes);

        RefreshToken entity = new RefreshToken();
        entity.setTokenId(tokenId);
        entity.setTokenHash(hash);
        entity.setUser(user.getStoredUser());
        entity.setCreatedAt(Instant.now());
        entity.setExpiresAt(Instant.now().plus(14, ChronoUnit.DAYS));

        repository.save(entity);

        return tokenId + "." + secretEncoded;
    }

    public String issue(SecurityUser user) {

        repository.revokeAllActiveByUser(user.getId(),Instant.now());
        String token = create(user);
        return token;
    }

    public String refresh(String rawToken) throws RuntimeException{

        String[] parts = rawToken.split("\\.");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid token format");
        }

        String tokenId = parts[0];
        byte[] secretBytes = Base64.getUrlDecoder().decode(parts[1]);

        RefreshToken token = repository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (token.isRevoked()) {

            throw new RuntimeException("Token revoked - possible compromise");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Token expired");
        }

        String providedHash = crypto.hash(secretBytes);

        if (!crypto.constantTimeEquals(providedHash, token.getTokenHash())) {
            throw new RuntimeException("Invalid token secret");
        }

        token.setRevoked(true);
        token.setRevokedAt(Instant.now());

        String newToken = create(new SecurityUser(token.getUser()));
        token.setReplacedByTokenId(newToken.split("\\.")[0]);

        repository.save(token);

        return newToken;
    }

    public SecurityUser extractUser(String rawToken) throws RuntimeException{

        String[] parts = rawToken.split("\\.");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid token format");
        }

        String tokenId = parts[0];
        byte[] secretBytes = Base64.getUrlDecoder().decode(parts[1]);

        RefreshToken token = repository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        return new SecurityUser(token.getUser());
    }
}




