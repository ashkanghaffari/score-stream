package org.ashkan.ghaffari.adminconsole.repository;

import org.ashkan.ghaffari.adminconsole.entity.IntegrationApiKey;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationApiKeyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationApiKeyRepository extends JpaRepository<IntegrationApiKey, IntegrationApiKeyId> {
    default IntegrationApiKey find(String tenantId, String integrationId, String apiKeyId) {
        return findById(new IntegrationApiKeyId(tenantId, integrationId, apiKeyId)).orElse(null);
    }

    List<IntegrationApiKey> findByTenantIdAndIntegrationId(String tenantId, String integrationId);
}
