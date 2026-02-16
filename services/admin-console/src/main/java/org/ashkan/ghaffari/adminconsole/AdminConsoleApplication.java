package org.ashkan.ghaffari.adminconsole;

import org.ashkan.ghaffari.adminconsole.security.config.properties.GoogleIdpProperties;
import org.ashkan.ghaffari.adminconsole.security.config.properties.JwtTokenProperties;
import org.ashkan.ghaffari.adminconsole.security.config.properties.IntegrationApiKeyPolicyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableConfigurationProperties({GoogleIdpProperties.class, JwtTokenProperties.class, IntegrationApiKeyPolicyProperties.class})
@EnableMethodSecurity
public class AdminConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminConsoleApplication.class, args);
    }
}
