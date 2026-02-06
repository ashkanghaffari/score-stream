package org.ashkan.ghaffari.adminconsole.security.config;

import org.ashkan.ghaffari.adminconsole.security.filter.InternalTokenFilter;
import org.ashkan.ghaffari.adminconsole.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final InternalTokenFilter internalTokenFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          InternalTokenFilter internalTokenFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.internalTokenFilter = internalTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/actuator/health/**",
                    "/actuator/info",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/auth/**",
                    "/v1/integration/api-keys/validate"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedEntryPoint()))
            .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
    }
}
