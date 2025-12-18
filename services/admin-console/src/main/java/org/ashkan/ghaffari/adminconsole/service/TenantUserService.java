package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantUserService {
    private TenantUserRepository tenantUserRepository;

    public TenantUserService(TenantUserRepository tenantUserRepository) {
        this.tenantUserRepository = tenantUserRepository;
    }
}
