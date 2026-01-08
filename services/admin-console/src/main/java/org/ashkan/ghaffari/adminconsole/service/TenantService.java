package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.dto.request.CreateTenantRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.TenantResponse;
import org.ashkan.ghaffari.adminconsole.entity.Tenant;
import org.ashkan.ghaffari.adminconsole.entity.TenantStatus;
import org.ashkan.ghaffari.adminconsole.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import java.time.Instant;
import java.util.List;
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
            tenant.getStatus().name(),
            tenant.getCreatedAt()
        );
    }

    public TenantResponse createTenant(CreateTenantRequest request) {
        Tenant tenant = new Tenant(
            UUID.randomUUID().toString(),
            request.name(),
            TenantStatus.ACTIVE,
            Instant.now().toEpochMilli(),
            null
        );

        tenantRepository.save(tenant);
        return new TenantResponse(
            tenant.getTenantId(),
            tenant.getName(),
            tenant.getStatus().toString(),
            tenant.getCreatedAt()
        );
    }

    public List<TenantResponse> getTenants() {
        return tenantRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
            tenant.getTenantId(),
            tenant.getName(),
            tenant.getStatus().name(),
            tenant.getCreatedAt()
        );
    }

    public TenantResponse changeStatus(String id, TenantStatus status) {
        Tenant tenant = tenantRepository.getById(id);
        if (tenant == null) {
            throw new ResponseStatusException(NOT_FOUND, "Tenant not found: " + id);
        }

        tenant.setStatus(status);
        tenantRepository.save(tenant);

        return toResponse(tenant);
    }

}
