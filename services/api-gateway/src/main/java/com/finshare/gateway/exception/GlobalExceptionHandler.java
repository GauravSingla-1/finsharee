package com.finshare.gateway.exception;

import com.finshare.gateway.model.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * Global exception handler for the API Gateway.
 * Provides consistent error responses for all unhandled exceptions.
 */
@Component
@Order(-1) // High priority to handle exceptions before default handlers
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        // Determine appropriate HTTP status and error message
        HttpStatus status;
        String message;
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : "An error occurred";
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal server error";
            logger.error("Unhandled exception in API Gateway: ", ex);
        }

        // Create error response
        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getPath().value(),
                Instant.now()
        );

        // Set response status and headers
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        // Serialize error response to JSON
        try {
            String errorJson = objectMapper.writeValueAsString(apiError);
            DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException jsonEx) {
            logger.error("Failed to serialize error response", jsonEx);
            
            // Fallback to simple error message
            String fallbackError = "{\"error\":\"Internal Server Error\",\"message\":\"Unable to process error response\"}";
            DataBuffer buffer = response.bufferFactory().wrap(fallbackError.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }
}
