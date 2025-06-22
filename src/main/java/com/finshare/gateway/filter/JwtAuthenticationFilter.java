package com.finshare.gateway.filter;

import com.finshare.gateway.service.FirebaseJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter for validating Firebase JWT tokens.
 * This filter intercepts all requests and validates the JWT token in the Authorization header.
 * On successful validation, it sets the security context with user information.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHENTICATED_USER_HEADER = "X-Authenticated-User-ID";

    @Autowired
    private FirebaseJwtService firebaseJwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Skip authentication for health check endpoints
        if (path.equals("/health") || path.startsWith("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from Authorization header
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Missing or invalid Authorization header for request: {}", path);
            handleUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        
        try {
            // Validate JWT token using Firebase service (blocking call for servlet filter)
            String uid = firebaseJwtService.validateTokenSync(token);
            logger.debug("Successfully authenticated user: {} for path: {}", uid, path);
            
            // Set authentication in security context
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    uid, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Add user ID header for downstream services
            request.setAttribute(AUTHENTICATED_USER_HEADER, uid);
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("JWT validation failed for path: {} - {}", path, e.getMessage());
            handleUnauthorized(response, "Invalid or expired token");
        }
    }

    /**
     * Handle unauthorized requests by returning 401 status with error message.
     */
    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        
        String errorBody = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\", \"timestamp\": \"%s\"}", 
                message, java.time.Instant.now().toString());
        
        response.getWriter().write(errorBody);
        response.getWriter().flush();
    }
}
