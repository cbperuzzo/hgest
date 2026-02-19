package com.lumem.hgest.model.Util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenCrypto {

    private final SecureRandom secureRandom = new SecureRandom();

    public byte[] generateSecret() {
        byte[] bytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public String hash(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input);
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                Base64.getDecoder().decode(a),
                Base64.getDecoder().decode(b)
        );
    }
}

