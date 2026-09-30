package com.lumem.hgest.security.token;

import com.lumem.hgest.model.RefreshToken;
import com.lumem.hgest.repository.RefreshTokenRepository;
import com.lumem.hgest.repository.StoredUserRepository;
import com.lumem.hgest.security.SecurityUser;
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
    private final StoredUserRepository storedUserRepository;

    public RefreshTokenService(RefreshTokenRepository repository,
                               RefreshTokenCrypto crypto,
                               StoredUserRepository storedUserRepository) {
        this.repository = repository;
        this.crypto = crypto;
        this.storedUserRepository = storedUserRepository;
    }

    public String create(SecurityUser user) {

        String tokenId = UUID.randomUUID().toString();
        byte[] secretBytes = crypto.generateSecret();

        String secretEncoded = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(secretBytes);

        String hash = crypto.hash(secretBytes);

        RefreshToken token = new RefreshToken();
        token.setTokenId(tokenId);
        token.setTokenHash(hash);
        token.setUser(storedUserRepository.getReferenceById(user.getId()));
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plus(14, ChronoUnit.DAYS));

        repository.save(token);

        return tokenId + "." + secretEncoded;
    }

    public String issue(SecurityUser user) {

        return create(user);
    }

    @Transactional(noRollbackFor = TokenReuseException.class)
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

            repository.revokeAllActiveByUser(token.getUser().getId(), Instant.now());

            throw new TokenReuseException("Token revoked - possible compromise, all active tokens for this user were revoked");
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

    public void revoke(String rawToken) throws RuntimeException{

        RefreshToken token = verify(rawToken);

        if (token.isRevoked())
            return;

        token.setRevoked(true);
        token.setRevokedAt(Instant.now());

        repository.save(token);
    }

    public void revokeAllSessions(String rawToken) throws RuntimeException{

        RefreshToken token = verify(rawToken);

        repository.revokeAllActiveByUser(token.getUser().getId(), Instant.now());
    }

    private RefreshToken verify(String rawToken) throws RuntimeException{

        String[] parts = rawToken.split("\\.");
        if (parts.length != 2) {
            throw new RuntimeException("Invalid token format");
        }

        byte[] secretBytes = Base64.getUrlDecoder().decode(parts[1]);

        RefreshToken token = repository.findByTokenId(parts[0])
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!crypto.constantTimeEquals(crypto.hash(secretBytes), token.getTokenHash())) {
            throw new RuntimeException("Invalid token secret");
        }

        return token;
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




