package com.finshare.notifications.controller;

import com.finshare.notifications.consumer.PubSubEventConsumer;
import com.finshare.notifications.dto.NotificationEventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for development testing of notification service
 * In production, this service would only consume Pub/Sub events
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private PubSubEventConsumer pubSubEventConsumer;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification service is healthy");
    }

    @PostMapping("/simulate/new-expense")
    public ResponseEntity<String> simulateNewExpense(@RequestBody NotificationEventDto event) {
        pubSubEventConsumer.simulateNewExpenseEvent(event);
        return ResponseEntity.ok("New expense notification simulated");
    }

    @PostMapping("/simulate/payment-recorded")
    public ResponseEntity<String> simulatePaymentRecorded(@RequestBody NotificationEventDto event) {
        pubSubEventConsumer.simulatePaymentRecordedEvent(event);
        return ResponseEntity.ok("Payment recorded notification simulated");
    }

    @PostMapping("/simulate/budget-alert")
    public ResponseEntity<String> simulateBudgetAlert(@RequestBody NotificationEventDto event) {
        pubSubEventConsumer.simulateBudgetAlertEvent(event);
        return ResponseEntity.ok("Budget alert notification simulated");
    }
}