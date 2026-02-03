package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.dto.request.CreateTenantUserRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.TenantUserResponse;
import org.ashkan.ghaffari.adminconsole.entity.TenantUser;
import org.ashkan.ghaffari.adminconsole.repository.TenantUserRepository;
import org.ashkan.ghaffari.adminconsole.entity.TenantUserRole;
import org.ashkan.ghaffari.adminconsole.entity.TenantUserStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class TenantUserService {
    private final TenantUserRepository tenantUserRepository;

    public TenantUserService(TenantUserRepository tenantUserRepository) {
        this.tenantUserRepository = tenantUserRepository;
    }

    public TenantUserResponse addUser(String tenantId, CreateTenantUserRequest request) {
        return createUser(tenantId, request, TenantUserStatus.ACTIVE);
    }

    public TenantUserResponse inviteUser(String tenantId, CreateTenantUserRequest request) {
        return createUser(tenantId, request, TenantUserStatus.INVITED);
    }

    public TenantUserResponse getUser(String tenantId, String userId) {
        TenantUser user = tenantUserRepository.find(tenantId, userId);
        if (user == null) {
            throw new ResponseStatusException(NOT_FOUND, "Tenant user not found: " + userId);
        }
        return toResponse(user);
    }

    private TenantUserResponse createUser(String tenantId, CreateTenantUserRequest request, TenantUserStatus status) {
        if (tenantUserRepository.findByTenantIdAndEmail(tenantId, request.email()).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "User already exists for tenant: " + request.email());
        }
        TenantUserRole role = request.role();
        if (role == TenantUserRole.SUPERADMIN && !isCallerSuperadmin()) {
            throw new ResponseStatusException(FORBIDDEN, "Only SUPERADMIN can create SUPERADMIN users");
        }
        TenantUser user = new TenantUser(
            tenantId,
            UUID.randomUUID().toString(),
            request.email(),
            role,
            status,
            Instant.now().toEpochMilli()
        );
        tenantUserRepository.save(user);
        return toResponse(user);
    }

    private boolean isCallerSuperadmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_SUPERADMIN".equals(authority.getAuthority()));
    }

    private TenantUserResponse toResponse(TenantUser user) {
        return new TenantUserResponse(
            user.getTenantId(),
            user.getUserId(),
            user.getEmail(),
            user.getRole(),
            user.getStatus(),
            user.getCreatedAt()
        );
    }

    public TenantUser requireActiveUser(String tenantId, String email) {
        return tenantUserRepository.findByTenantIdAndEmail(tenantId, email)
            .filter(user -> user.getStatus() == TenantUserStatus.ACTIVE)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                "User not active for tenant: " + tenantId));
    }

    public TenantUser requireActiveUserById(String tenantId, String userId) {
        TenantUser user = tenantUserRepository.find(tenantId, userId);
        if (user == null || user.getStatus() != TenantUserStatus.ACTIVE) {
            throw new ResponseStatusException(UNAUTHORIZED, "User not active for tenant: " + tenantId);
        }
        return user;
    }
}
