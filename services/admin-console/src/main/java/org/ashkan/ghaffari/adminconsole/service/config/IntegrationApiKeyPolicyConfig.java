package org.ashkan.ghaffari.adminconsole.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin.integration.api-keys")
public class IntegrationApiKeyPolicyConfig {
    private Integer defaultTtlDays;

    public Integer getDefaultTtlDays() {
        return defaultTtlDays;
    }

    public void setDefaultTtlDays(Integer defaultTtlDays) {
        this.defaultTtlDays = defaultTtlDays;
    }
}
