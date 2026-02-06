package org.ashkan.ghaffari.adminconsole.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {

    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";
    private static final String VALIDATION_PATH = "/v1/integration/api-keys/validate";

    @Value("${internal.service.token}")
    private String expectedToken;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String provided = request.getHeader(INTERNAL_TOKEN_HEADER);

        if (provided != null &&
            expectedToken != null &&
            MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                provided.getBytes(StandardCharsets.UTF_8)
            )
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith(VALIDATION_PATH);
    }
}
