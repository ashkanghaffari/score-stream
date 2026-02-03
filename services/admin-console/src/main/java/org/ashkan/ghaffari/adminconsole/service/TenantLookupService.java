package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.entity.Tenant;
import org.ashkan.ghaffari.adminconsole.repository.TenantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class TenantLookupService {
    private final TenantRepository tenantRepository;

    public TenantLookupService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Optional<Tenant> findByName(String tenantName) {
        if (!StringUtils.hasText(tenantName)) {
            return Optional.empty();
        }
        return tenantRepository.findByName(tenantName);
    }

    public Tenant requireByName(String tenantName) {
        if (!StringUtils.hasText(tenantName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing tenantName");
        }
        return tenantRepository.findByName(tenantName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found: " + tenantName));
    }

    public String requireTenantId(String tenantName) {
        return requireByName(tenantName).getTenantId();
    }
}
