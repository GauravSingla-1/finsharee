package com.finshare.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for the API Gateway.
 * Provides endpoints for monitoring gateway health and status.
 */
@RestController
public class HealthController {

    /**
     * Basic health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "FinShare API Gateway");
        health.put("timestamp", Instant.now().toString());
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }
}