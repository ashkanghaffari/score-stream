package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.dto.response.CreateIntegrationApiKeyResponse;
import org.ashkan.ghaffari.adminconsole.dto.response.IntegrationApiKeyResponse;
import org.ashkan.ghaffari.adminconsole.entity.Integration;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationApiKey;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationApiKeyStatus;
import org.ashkan.ghaffari.adminconsole.repository.IntegrationApiKeyRepository;
import org.ashkan.ghaffari.adminconsole.repository.IntegrationRepository;
import org.ashkan.ghaffari.adminconsole.service.config.IntegrationApiKeyPolicyConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class IntegrationApiKeyService {
    private static final int KEY_BYTES = 32;

    private final IntegrationRepository integrationRepository;
    private final IntegrationApiKeyRepository integrationApiKeyRepository;
    private final IntegrationApiKeyPolicyConfig policyConfig;
    private final SecureRandom secureRandom = new SecureRandom();

    public IntegrationApiKeyService(IntegrationRepository integrationRepository,
                                    IntegrationApiKeyRepository integrationApiKeyRepository,
                                    IntegrationApiKeyPolicyConfig policyConfig) {
        this.integrationRepository = integrationRepository;
        this.integrationApiKeyRepository = integrationApiKeyRepository;
        this.policyConfig = policyConfig;
    }

    public CreateIntegrationApiKeyResponse create(String tenantId, String integrationId) {
        Integration integration = integrationRepository.find(tenantId, integrationId);
        if (integration == null) {
            throw new ResponseStatusException(NOT_FOUND, "Integration not found: " + integrationId);
        }

        String apiKeyId = UUID.randomUUID().toString();
        String plainKey = generateApiKey();
        String keyHash = hashKey(plainKey);
        Long createdAt = Instant.now().toEpochMilli();

        Long expiresAt = resolveExpiresAt(createdAt);
        IntegrationApiKey apiKey = new IntegrationApiKey(
            tenantId,
            integrationId,
            apiKeyId,
            keyHash,
            IntegrationApiKeyStatus.ACTIVE,
            createdAt,
            expiresAt,
            null
        );

        integrationApiKeyRepository.save(apiKey);

        return new CreateIntegrationApiKeyResponse(
            apiKeyId,
            plainKey,
            apiKey.getStatus(),
            createdAt,
            apiKey.getExpiresAt()
        );
    }

    public List<IntegrationApiKeyResponse> list(String tenantId, String integrationId) {
        return integrationApiKeyRepository.findByTenantIdAndIntegrationId(tenantId, integrationId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public void revoke(String tenantId, String integrationId, String apiKeyId) {
        IntegrationApiKey apiKey = integrationApiKeyRepository.find(tenantId, integrationId, apiKeyId);
        if (apiKey == null) {
            throw new ResponseStatusException(NOT_FOUND, "API key not found: " + apiKeyId);
        }
        apiKey.setStatus(IntegrationApiKeyStatus.REVOKED);
        integrationApiKeyRepository.save(apiKey);
    }

    private IntegrationApiKeyResponse toResponse(IntegrationApiKey apiKey) {
        return new IntegrationApiKeyResponse(
            apiKey.getApiKeyId(),
            apiKey.getKeyHash(),
            apiKey.getStatus(),
            apiKey.getCreatedAt(),
            apiKey.getExpiresAt(),
            apiKey.getLastUsedAt()
        );
    }

    private String generateApiKey() {
        byte[] bytes = new byte[KEY_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashKey(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("Error hashing API key", ex);
        }
    }

    private Long resolveExpiresAt(long createdAt) {
        Integer ttlDays = policyConfig.getDefaultTtlDays();
        if (ttlDays == null || ttlDays <= 0) {
            return null;
        }
        return Instant.ofEpochMilli(createdAt)
            .plusSeconds(ttlDays * 86400L)
            .toEpochMilli();
    }
}
