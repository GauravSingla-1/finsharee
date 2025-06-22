package com.finshare.gateway.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Service responsible for proxying requests to downstream microservices.
 * Handles routing, header forwarding, and response processing.
 */
@Service
public class ProxyService {

    private static final Logger logger = LoggerFactory.getLogger(ProxyService.class);

    @Autowired
    private RestTemplate restTemplate;

    private final Map<String, String> serviceUrls = Map.of(
            "/api/users", "http://localhost:8001",
            "/api/groups", "http://localhost:8002",
            "/api/expenses", "http://localhost:8002",
            "/api/balances", "http://localhost:8003",
            "/api/settlements", "http://localhost:8003",
            "/api/ai", "http://localhost:8004",
            "/api/analytics", "http://localhost:8005",
            "/api/budgets", "http://localhost:8005"
    );

    /**
     * Proxy an HTTP request to the appropriate downstream service.
     *
     * @param request    The original HTTP request
     * @param method     HTTP method
     * @param body       Request body (for POST/PUT/PATCH)
     * @return ResponseEntity with the response from downstream service
     */
    public ResponseEntity<?> proxyRequest(HttpServletRequest request, String method, String body) {
        try {
            String path = request.getRequestURI();
            String targetUrl = determineTargetUrl(path);
            
            if (targetUrl == null) {
                logger.warn("No service mapping found for path: {}", path);
                return ResponseEntity.notFound().build();
            }

            // For AI service, keep the full path; for others, remove /api prefix
            String servicePath;
            if (path.startsWith("/api/ai")) {
                servicePath = path; // Keep full path for AI service
            } else {
                servicePath = path.substring(4); // Remove "/api" for other services
            }
            String fullUrl = targetUrl + servicePath;
            
            // Add query parameters if present
            if (request.getQueryString() != null) {
                fullUrl += "?" + request.getQueryString();
            }

            logger.debug("Proxying {} request from {} to {}", method, path, fullUrl);

            // Forward headers (except Host)
            HttpHeaders headers = forwardHeaders(request);
            
            // Add authenticated user header if available
            String userId = (String) request.getAttribute("X-Authenticated-User-ID");
            if (userId != null) {
                headers.add("X-Authenticated-User-ID", userId);
            }
            
            // Create HTTP entity
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            
            // Make the request
            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(fullUrl),
                    HttpMethod.valueOf(method),
                    entity,
                    String.class
            );

            logger.debug("Received response with status: {}", response.getStatusCode());
            return response;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("HTTP error while proxying request: {} - {}", e.getStatusCode(), e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Error proxying request to {}: {}", request.getRequestURI(), e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\": \"Service temporarily unavailable\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Determine the target service URL based on the request path.
     */
    private String determineTargetUrl(String path) {
        for (Map.Entry<String, String> entry : serviceUrls.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Forward relevant headers from the original request to downstream services.
     */
    private HttpHeaders forwardHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            
            // Skip certain headers that shouldn't be forwarded
            if (shouldForwardHeader(headerName)) {
                String headerValue = request.getHeader(headerName);
                headers.add(headerName, headerValue);
            }
        }
        
        return headers;
    }

    /**
     * Determine if a header should be forwarded to downstream services.
     */
    private boolean shouldForwardHeader(String headerName) {
        String lowerHeaderName = headerName.toLowerCase();
        
        // Don't forward these headers
        return !lowerHeaderName.equals("host") && 
               !lowerHeaderName.equals("content-length") &&
               !lowerHeaderName.equals("transfer-encoding") &&
               !lowerHeaderName.equals("connection");
    }
}