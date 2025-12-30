package org.ashkan.ghaffari.adminconsole.service;

import org.ashkan.ghaffari.adminconsole.repository.IntegrationRepository;
import org.springframework.stereotype.Service;

@Service
public class IntegrationService {
    private final IntegrationRepository integrationRepository;

    public IntegrationService(IntegrationRepository integrationRepository) {
        this.integrationRepository = integrationRepository;
    }
}
