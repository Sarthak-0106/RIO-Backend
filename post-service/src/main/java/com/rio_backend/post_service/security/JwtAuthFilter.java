package com.rio_backend.post_service.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        log.info("🔍 Incoming request: [{} {}]", request.getMethod(), path);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Authorization header found or invalid format");
            filterChain.doFilter(request, response); // allow public endpoints
            return;
        }

        String token = authHeader.substring(7);
        log.debug("Extracted token: {}", token.substring(0, Math.min(15, token.length())) + "...");

        try {
            jwtService.validateToken(token);
            log.info("Token validated successfully");
            setAuthentication(token, request);
        } catch (ExpiredJwtException e) {
            log.warn("Access token expired for path {}", path);

            try {
                String email = jwtService.extractEmail(token);
                log.info("Requesting new access token from auth-service for {}", email);

                Map<String, String> payload = Map.of("email", email);
                Map<?, ?> newTokenResponse = restTemplate.postForObject(
                        "http://localhost:8081/api/auth/refresh", // or whatever endpoint you expose
                        payload,
                        Map.class
                );

                String newAccessToken = (String) newTokenResponse.get("accessToken");
                if (newAccessToken != null) {
                    log.info("Token refreshed successfully");
                    jwtService.validateToken(newAccessToken);
                    setAuthentication(newAccessToken, request);
                    response.setHeader("X-New-Access-Token", newAccessToken);
                } else {
                    log.error("Auth service did not return access token");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token refresh failed");
                    return;
                }

            } catch (Exception ex) {
                log.error("Could not refresh token: {}", ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token refresh failed");
                return;
            }
        }

        log.debug("Continuing request chain for {}", path);
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        String email = jwtService.extractEmail(token);
        log.debug("Authenticated user: {}", email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        new User(email, "", Collections.emptyList()),
                        null,
                        Collections.emptyList()
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
