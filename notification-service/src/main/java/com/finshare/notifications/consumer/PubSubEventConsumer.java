package com.finshare.notifications.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finshare.notifications.dto.NotificationEventDto;
import com.finshare.notifications.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Event consumer for Google Cloud Pub/Sub messages
 * In development, provides REST endpoints to simulate Pub/Sub events
 */
@Component
public class PubSubEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PubSubEventConsumer.class);

    @Autowired
    private NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Process incoming Pub/Sub message
     * In production, this would be annotated with @PubsubListener
     */
    public void handlePubSubMessage(String messagePayload) {
        try {
            logger.info("Received Pub/Sub message: {}", messagePayload);
            
            NotificationEventDto event = objectMapper.readValue(messagePayload, NotificationEventDto.class);
            notificationService.processNotificationEvent(event);
            
        } catch (Exception e) {
            logger.error("Failed to process Pub/Sub message: {}", e.getMessage(), e);
        }
    }

    /**
     * Simulate new expense event for development
     */
    public void simulateNewExpenseEvent(NotificationEventDto event) {
        logger.info("Simulating NEW_EXPENSE event for development");
        event.setEventType("NEW_EXPENSE");
        notificationService.processNotificationEvent(event);
    }

    /**
     * Simulate payment recorded event for development
     */
    public void simulatePaymentRecordedEvent(NotificationEventDto event) {
        logger.info("Simulating PAYMENT_RECORDED event for development");
        event.setEventType("PAYMENT_RECORDED");
        notificationService.processNotificationEvent(event);
    }

    /**
     * Simulate budget alert event for development
     */
    public void simulateBudgetAlertEvent(NotificationEventDto event) {
        logger.info("Simulating BUDGET_ALERT event for development");
        event.setEventType("BUDGET_ALERT");
        notificationService.processNotificationEvent(event);
    }
}