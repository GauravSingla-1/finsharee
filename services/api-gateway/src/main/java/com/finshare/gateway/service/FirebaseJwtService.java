package com.finshare.gateway.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Service for validating Firebase JWT tokens.
 * Handles Firebase initialization and token validation.
 */
@Service
public class FirebaseJwtService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseJwtService.class);

    @Value("${firebase.project-id:${FIREBASE_PROJECT_ID:finshare-app}}")
    private String firebaseProjectId;

    @Value("${firebase.service-account-key:${FIREBASE_SERVICE_ACCOUNT_KEY:}}")
    private String serviceAccountKey;

    private FirebaseAuth firebaseAuth;

    /**
     * Initialize Firebase Admin SDK.
     * This method is called after dependency injection is complete.
     */
    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                        .setProjectId(firebaseProjectId);

                // For development, skip Firebase initialization if no credentials are provided
                if (serviceAccountKey != null && !serviceAccountKey.isEmpty()) {
                    try {
                        GoogleCredentials credentials = GoogleCredentials.fromStream(
                                new ByteArrayInputStream(serviceAccountKey.getBytes(StandardCharsets.UTF_8)));
                        optionsBuilder.setCredentials(credentials);
                        logger.info("Firebase initialized with service account credentials");
                    } catch (IOException e) {
                        logger.warn("Failed to load service account key: {}", e.getMessage());
                        // For development, we'll create a mock setup
                        logger.info("Running in development mode without Firebase credentials");
                        return;
                    }
                } else {
                    // For development, skip Firebase initialization
                    logger.info("Running in development mode without Firebase credentials");
                    return;
                }

                FirebaseApp.initializeApp(optionsBuilder.build());
                this.firebaseAuth = FirebaseAuth.getInstance();
                logger.info("Firebase Auth service initialized successfully for project: {}", firebaseProjectId);
            }

        } catch (Exception e) {
            logger.warn("Firebase initialization failed, running in development mode: {}", e.getMessage());
            // Continue without Firebase for development
        }
    }

    /**
     * Validate a Firebase JWT token and extract the user ID (reactive version).
     * 
     * @param token The JWT token to validate
     * @return Mono<String> containing the user ID if validation succeeds
     */
    public Mono<String> validateToken(String token) {
        return Mono.fromCallable(() -> validateTokenSync(token))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Validate a Firebase JWT token and extract the user ID (synchronous version).
     * 
     * @param token The JWT token to validate
     * @return String containing the user ID if validation succeeds
     * @throws RuntimeException if validation fails
     */
    public String validateTokenSync(String token) {
        try {
            // If Firebase is not initialized (development mode), create a mock user ID
            if (firebaseAuth == null) {
                logger.debug("Running in development mode, creating mock user ID");
                return "dev-user-" + Math.abs(token.hashCode() % 1000);
            }
            
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
            String uid = decodedToken.getUid();
            
            logger.debug("Successfully validated token for user: {}", uid);
            return uid;
            
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            throw new RuntimeException("Invalid token: " + e.getMessage(), e);
        }
    }

    /**
     * Get the Firebase project ID.
     * 
     * @return The configured Firebase project ID
     */
    public String getProjectId() {
        return firebaseProjectId;
    }
}
