package org.ashkan.ghaffari.adminconsole.security.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin.integration.api-keys")
public class IntegrationApiKeyPolicyProperties {
    private Integer defaultTtlDays;

    public Integer getDefaultTtlDays() {
        return defaultTtlDays;
    }

    public void setDefaultTtlDays(Integer defaultTtlDays) {
        this.defaultTtlDays = defaultTtlDays;
    }
}
