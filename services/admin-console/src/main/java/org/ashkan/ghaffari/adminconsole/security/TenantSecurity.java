package org.ashkan.ghaffari.adminconsole.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import org.ashkan.ghaffari.adminconsole.service.TenantLookupService;

@Component
public class TenantSecurity {

    private final TenantLookupService tenantLookupService;

    public TenantSecurity(TenantLookupService tenantLookupService) {
        this.tenantLookupService = tenantLookupService;
    }

    public boolean canAccessTenant(String tenantId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        if (hasRole(auth, "ROLE_SUPERADMIN")) {
            return true;
        }
        Object details = auth.getDetails();
        if (details instanceof Map<?, ?> map) {
            Object tokenTenantId = map.get("tenantId");
            return tenantId != null && tenantId.equals(tokenTenantId);
        }
        return false;
    }

    public boolean canAccessTenantName(String tenantName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && hasRole(auth, "ROLE_SUPERADMIN")) {
            return true;
        }
        Optional<String> tenantId = tenantLookupService.findByName(tenantName)
            .map(tenant -> tenant.getTenantId());
        return tenantId.map(this::canAccessTenant).orElse(false);
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
            .anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}
