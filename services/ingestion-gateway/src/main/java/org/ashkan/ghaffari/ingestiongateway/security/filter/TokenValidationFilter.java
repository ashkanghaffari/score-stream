package org.ashkan.ghaffari.ingestiongateway.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenValidationFilter extends OncePerRequestFilter {

    public TokenValidationFilter() {

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // take raw API key and hash it
        // check the hash key against redis, if cache hit and ttl within range accept
        // if cache miss, use client to make request to admin-console. If admin console fails api call deny
        // If admin console comes back with ALLOW, update cache with TLL given by admin console and allow request

        filterChain.doFilter(request, response);
    }
}
