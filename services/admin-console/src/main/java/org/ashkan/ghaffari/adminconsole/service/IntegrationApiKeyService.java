package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.dto.response.CreateIntegrationApiKeyResponse;
import org.ashkan.ghaffari.adminconsole.dto.response.IntegrationApiKeyResponse;
import org.ashkan.ghaffari.adminconsole.dto.response.ValidateApiKeyResponse;
import org.ashkan.ghaffari.adminconsole.entity.Integration;
import org.ashkan.ghaffari.adminconsole.dto.response.ValidateApiKeyReason;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationApiKey;
import org.ashkan.ghaffari.common.integration.IntegrationApiKeyStatus;
import org.ashkan.ghaffari.adminconsole.repository.IntegrationApiKeyRepository;
import org.ashkan.ghaffari.adminconsole.repository.IntegrationRepository;
import org.ashkan.ghaffari.adminconsole.service.config.IntegrationApiKeyPolicyConfig;
import org.ashkan.ghaffari.common.security.ApiKeyHasher;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        return ApiKeyHasher.hashKey(key);
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

    public ValidateApiKeyResponse validateApiKey(String keyHash) {
        IntegrationApiKey integrationApiKey = integrationApiKeyRepository.findByKeyHash(keyHash).orElse(null);
        if (integrationApiKey == null) {
            return invalidResponse(ValidateApiKeyReason.NOT_FOUND);
        }
        if (integrationApiKey.getStatus() == IntegrationApiKeyStatus.REVOKED) {
            return invalidResponse(ValidateApiKeyReason.REVOKED);
        }
        Long expiresAt = integrationApiKey.getExpiresAt();
        if (expiresAt != null && expiresAt <= Instant.now().toEpochMilli()) {
            return invalidResponse(ValidateApiKeyReason.EXPIRED);
        }
        return new ValidateApiKeyResponse(
            true,
            integrationApiKey.getTenantId(),
            integrationApiKey.getIntegrationId(),
            integrationApiKey.getStatus(),
            integrationApiKey.getExpiresAt(),
            null
        );
    }

    private ValidateApiKeyResponse invalidResponse(ValidateApiKeyReason reason) {
        return new ValidateApiKeyResponse(
            false,
            null,
            null,
            null,
            null,
            reason
        );
    }
}
