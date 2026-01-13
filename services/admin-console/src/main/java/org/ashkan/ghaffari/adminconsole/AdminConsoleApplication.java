package org.ashkan.ghaffari.adminconsole;

import org.ashkan.ghaffari.adminconsole.security.config.GoogleIdpConfig;
import org.ashkan.ghaffari.adminconsole.security.token.JwtTokenConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableConfigurationProperties({GoogleIdpConfig.class, JwtTokenConfig.class})
@EnableMethodSecurity
public class AdminConsoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminConsoleApplication.class, args);
    }
}
