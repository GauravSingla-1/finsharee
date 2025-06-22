package com.finshare.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FinShare Notification Service Application
 * 
 * Handles push notifications via Firebase Cloud Messaging
 * Consumes events from other services via Google Cloud Pub/Sub
 */
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}