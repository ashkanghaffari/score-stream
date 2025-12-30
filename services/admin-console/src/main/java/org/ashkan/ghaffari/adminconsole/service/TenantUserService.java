package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.dto.request.CreateTenantUserRequest;
import org.ashkan.ghaffari.adminconsole.dto.response.TenantUserResponse;
import org.ashkan.ghaffari.adminconsole.repository.TenantUserRepository;
import org.ashkan.ghaffari.common.adminconsole.TenantUserRole;
import org.ashkan.ghaffari.common.adminconsole.TenantUserStatus;
import org.ashkan.ghaffari.common.dynamo.tenantuser.TenantUser;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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
        TenantUserRole role = request.role();
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
}
