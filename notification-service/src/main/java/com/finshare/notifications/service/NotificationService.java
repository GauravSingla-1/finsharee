package com.finshare.notifications.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finshare.notifications.dto.NotificationEventDto;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;

/**
 * Core notification service handling Firebase Cloud Messaging
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Process notification event and send push notification
     */
    public void processNotificationEvent(NotificationEventDto event) {
        try {
            logger.info("Processing notification event: {}", event.getEventType());

            switch (event.getEventType()) {
                case "NEW_EXPENSE":
                    handleNewExpenseNotification(event);
                    break;
                case "PAYMENT_RECORDED":
                    handlePaymentRecordedNotification(event);
                    break;
                case "BUDGET_ALERT":
                    handleBudgetAlertNotification(event);
                    break;
                default:
                    logger.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            logger.error("Failed to process notification event: {}", e.getMessage(), e);
        }
    }

    private void handleNewExpenseNotification(NotificationEventDto event) {
        // Get the display name of the user who added the expense
        String addedByDisplayName = getUserDisplayName(event.getAddedByUserId());
        
        String title = "New Expense Added";
        String message = String.format("%s added $%.2f to '%s'", 
            addedByDisplayName, event.getAmount(), event.getGroupName());
        
        // Send notification to all involved users except the one who added it
        if (event.getInvolvedUserIds() != null) {
            event.getInvolvedUserIds().stream()
                .filter(userId -> !userId.equals(event.getAddedByUserId()))
                .forEach(userId -> sendPushNotification(userId, title, message));
        }
    }

    private void handlePaymentRecordedNotification(NotificationEventDto event) {
        String fromUserDisplayName = getUserDisplayName(event.getFromUserId());
        
        String title = "Payment Recorded";
        String message = String.format("%s recorded a payment of $%.2f to you", 
            fromUserDisplayName, event.getAmount());
        
        sendPushNotification(event.getToUserId(), title, message);
    }

    private void handleBudgetAlertNotification(NotificationEventDto event) {
        String title = "Budget Alert";
        String message = String.format("You've reached %d%% of your %s budget", 
            event.getPercentage(), event.getCategory());
        
        sendPushNotification(event.getUserId(), title, message);
    }

    private void sendPushNotification(String userId, String title, String messageBody) {
        try {
            // Get FCM token for user
            String fcmToken = getUserFcmToken(userId);
            if (fcmToken == null) {
                logger.warn("No FCM token found for user: {}", userId);
                return;
            }

            // Build the notification
            Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(messageBody)
                .build();

            // Build the message
            Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .build();

            // Send via Firebase (development mode - log only)
            if (isFirebaseConfigured()) {
                String response = FirebaseMessaging.getInstance().send(message);
                logger.info("Successfully sent message to user {}: {}", userId, response);
            } else {
                logger.info("DEVELOPMENT MODE - Would send notification to user {}: {} - {}", 
                    userId, title, messageBody);
            }

        } catch (Exception e) {
            logger.error("Failed to send push notification to user {}: {}", userId, e.getMessage());
        }
    }

    private String getUserDisplayName(String userId) {
        try {
            WebClient webClient = webClientBuilder.build();
            
            // Call User Service to get display name
            String response = webClient.get()
                .uri("http://localhost:8001/api/users/profile/" + userId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            if (response != null) {
                var userProfile = objectMapper.readTree(response);
                return userProfile.get("displayName").asText("Unknown User");
            }
        } catch (Exception e) {
            logger.warn("Failed to get display name for user {}: {}", userId, e.getMessage());
        }
        
        return "Unknown User";
    }

    private String getUserFcmToken(String userId) {
        try {
            WebClient webClient = webClientBuilder.build();
            
            // Call User Service to get FCM token
            String response = webClient.get()
                .uri("http://localhost:8001/api/users/fcm-token/" + userId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            if (response != null) {
                var tokenResponse = objectMapper.readTree(response);
                return tokenResponse.get("fcmToken").asText();
            }
        } catch (Exception e) {
            logger.debug("No FCM token found for user {}: {}", userId, e.getMessage());
        }
        
        return null;
    }

    private boolean isFirebaseConfigured() {
        try {
            return FirebaseMessaging.getInstance() != null;
        } catch (Exception e) {
            return false;
        }
    }
}