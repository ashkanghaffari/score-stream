package org.ashkan.ghaffari.adminconsole.controller;

import org.ashkan.ghaffari.adminconsole.service.TenantUserService;
import org.springframework.stereotype.Controller;

@Controller
public class TenantUserController {
    private TenantUserService tenantUserService;

    public TenantUserController(TenantUserService tenantUserService) {
        this.tenantUserService = tenantUserService;
    }
}
