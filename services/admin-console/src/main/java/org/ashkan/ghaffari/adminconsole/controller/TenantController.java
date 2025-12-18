package org.ashkan.ghaffari.adminconsole.controller;

import org.ashkan.ghaffari.adminconsole.service.TenantService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TenantController {
    private TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }
}
