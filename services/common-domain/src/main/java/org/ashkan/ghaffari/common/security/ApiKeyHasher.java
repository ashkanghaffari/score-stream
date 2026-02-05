package org.ashkan.ghaffari.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class ApiKeyHasher {
    private ApiKeyHasher() {}

    public static String hashKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("API key must not be null");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("Error hashing API key", ex);
        }
    }
}
