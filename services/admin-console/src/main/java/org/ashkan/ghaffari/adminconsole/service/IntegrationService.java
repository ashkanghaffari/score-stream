package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.dto.request.CreateIntegrationRequest;
import org.ashkan.ghaffari.adminconsole.dto.request.UpdateIntegrationStatusRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.IntegrationResponse;
import org.ashkan.ghaffari.adminconsole.entity.Integration;
import org.ashkan.ghaffari.adminconsole.repository.IntegrationRepository;
import org.ashkan.ghaffari.adminconsole.entity.IntegrationStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class IntegrationService {
    private final IntegrationRepository integrationRepository;

    public IntegrationService(IntegrationRepository integrationRepository) {
        this.integrationRepository = integrationRepository;
    }

    public IntegrationResponse create(String tenantId, CreateIntegrationRequest request) {
        Integration integration = new Integration(
            tenantId,
            UUID.randomUUID().toString(),
            request.name(),
            request.type(),
            IntegrationStatus.ACTIVE,
            Instant.now().toEpochMilli()
        );
        integrationRepository.save(integration);
        return toResponse(integration);
    }

    public IntegrationResponse get(String tenantId, String integrationId) {
        Integration integration = integrationRepository.find(tenantId, integrationId);
        if (integration == null) {
            throw new ResponseStatusException(NOT_FOUND, "Integration not found: " + integrationId);
        }
        return toResponse(integration);
    }

    public List<IntegrationResponse> list(String tenantId) {
        return integrationRepository.findByTenantId(tenantId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public IntegrationResponse updateStatus(String tenantId, String integrationId, UpdateIntegrationStatusRequest request) {
        Integration integration = integrationRepository.find(tenantId, integrationId);
        if (integration == null) {
            throw new ResponseStatusException(NOT_FOUND, "Integration not found: " + integrationId);
        }
        integration.setStatus(request.status());
        integrationRepository.save(integration);
        return toResponse(integration);
    }

    private IntegrationResponse toResponse(Integration integration) {
        return new IntegrationResponse(
            integration.getTenantId(),
            integration.getIntegrationId(),
            integration.getName(),
            integration.getType(),
            integration.getStatus(),
            integration.getCreatedAt()
        );
    }
}
