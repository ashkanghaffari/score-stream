package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.dto.request.CreateTenantRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.TenantResponse;
import org.ashkan.ghaffari.adminconsole.repository.TenantRepository;
import org.ashkan.ghaffari.common.dynamo.tenant.Tenant;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import java.time.Instant;
import java.util.UUID;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantResponse getTenant(String id) {
        Tenant tenant = tenantRepository.getById(id);

        if (tenant == null) {
            throw new ResponseStatusException(NOT_FOUND, "Tenant not found: " + id);
        }

        return new TenantResponse(
            tenant.getTenantId(),
            tenant.getName(),
            tenant.getStatus(),
            tenant.getCreatedAt()
        );
    }

    public TenantResponse createTenant(CreateTenantRequest request) {
        Tenant tenant = new Tenant(
            UUID.randomUUID().toString(),
            request.name(),
            "ACTIVE",
            Instant.now().toEpochMilli(),
            null
        );

        tenantRepository.save(tenant);
        return new TenantResponse(
            tenant.getTenantId(),
            tenant.getName(),
            tenant.getStatus(),
            tenant.getCreatedAt()
        );
    }
}
