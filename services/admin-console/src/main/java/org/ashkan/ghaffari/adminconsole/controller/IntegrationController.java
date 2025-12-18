package org.ashkan.ghaffari.adminconsole.controller;

import org.ashkan.ghaffari.adminconsole.service.IntegrationService;
import org.springframework.stereotype.Controller;

@Controller
public class IntegrationController {
    private IntegrationService integrationService;

    public IntegrationController(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }
}
