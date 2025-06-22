package com.finshare.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for the API Gateway.
 * Configures CORS, RestTemplate, and other web-related beans.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS to allow requests from mobile applications.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * RestTemplate bean for making HTTP requests to downstream services.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}