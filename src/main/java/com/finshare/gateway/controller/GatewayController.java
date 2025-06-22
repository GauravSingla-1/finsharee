package com.finshare.gateway.controller;

import com.finshare.gateway.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Main gateway controller that handles all API requests and routes them to appropriate microservices.
 * Acts as a proxy for all downstream services.
 */
@RestController
@RequestMapping("/api")
public class GatewayController {

    private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    @Autowired
    private ProxyService proxyService;

    /**
     * Handle all GET requests to microservices.
     */
    @GetMapping("/**")
    public ResponseEntity<?> handleGet(HttpServletRequest request) {
        logger.debug("Handling GET request: {}", request.getRequestURI());
        return proxyService.proxyRequest(request, "GET", null);
    }

    /**
     * Handle all POST requests to microservices.
     */
    @PostMapping("/**")
    public ResponseEntity<?> handlePost(HttpServletRequest request, @RequestBody(required = false) String body) {
        logger.debug("Handling POST request: {}", request.getRequestURI());
        return proxyService.proxyRequest(request, "POST", body);
    }

    /**
     * Handle all PUT requests to microservices.
     */
    @PutMapping("/**")
    public ResponseEntity<?> handlePut(HttpServletRequest request, @RequestBody(required = false) String body) {
        logger.debug("Handling PUT request: {}", request.getRequestURI());
        return proxyService.proxyRequest(request, "PUT", body);
    }

    /**
     * Handle all DELETE requests to microservices.
     */
    @DeleteMapping("/**")
    public ResponseEntity<?> handleDelete(HttpServletRequest request) {
        logger.debug("Handling DELETE request: {}", request.getRequestURI());
        return proxyService.proxyRequest(request, "DELETE", null);
    }

    /**
     * Handle all PATCH requests to microservices.
     */
    @PatchMapping("/**")
    public ResponseEntity<?> handlePatch(HttpServletRequest request, @RequestBody(required = false) String body) {
        logger.debug("Handling PATCH request: {}", request.getRequestURI());
        return proxyService.proxyRequest(request, "PATCH", body);
    }
}