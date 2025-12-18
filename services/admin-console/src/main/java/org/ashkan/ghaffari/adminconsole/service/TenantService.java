package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.repository.TenantRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantService {
    private TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }
}
