# Notification Service

## Overview
The Notification Service handles all real-time messaging and notification delivery across the FinShare platform, ensuring users stay informed about expense updates, payment requests, and important alerts.

## Architecture
- **Technology**: Spring Boot 3 with Java 17
- **Port**: 8006
- **Role**: Real-time notifications, message processing, multi-channel delivery

## Core Features

### 1. Real-time Notifications
Instant delivery of expense updates, payment requests, and group activities.

### 2. Multi-Channel Delivery
Support for push notifications, email, SMS, and in-app messaging.

### 3. Event-Driven Processing
Automatic notification triggers based on system events and user actions.

### 4. User Preferences
Customizable notification settings with granular control over message types.

## API Examples

### Send Notification

#### Expense Created Notification
```bash
POST http://localhost:8006/api/notifications/send
Content-Type: application/json
X-Authenticated-User-ID: system

{
  "type": "EXPENSE_CREATED",
  "recipientUserIds": ["user456", "user789"],
  "title": "New Expense Added",
  "message": "John added a $120 expense for 'Dinner at Sushi Restaurant' in Tokyo Trip group",
  "data": {
    "expenseId": "expense-123",
    "groupId": "group-456",
    "groupName": "Tokyo Trip",
    "amount": 120.00,
    "createdBy": "user123",
    "createdByName": "John Doe"
  },
  "channels": ["PUSH", "IN_APP"],
  "priority": "NORMAL"
}
```

**Response (202 Accepted):**
```json
{
  "notificationId": "notif-789",
  "status": "QUEUED",
  "recipientCount": 2,
  "scheduledDelivery": "2025-06-22T17:40:00Z",
  "channels": ["PUSH", "IN_APP"]
}
```

#### Payment Request Notification
```bash
POST http://localhost:8006/api/notifications/send
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "type": "PAYMENT_REQUEST",
  "recipientUserIds": ["user456"],
  "title": "Payment Request",
  "message": "John is requesting $83.33 for Tokyo Trip expenses",
  "data": {
    "amount": 83.33,
    "groupId": "group-456",
    "groupName": "Tokyo Trip",
    "requestedBy": "user123",
    "requestedByName": "John Doe",
    "dueDate": "2025-06-29T00:00:00Z",
    "paymentMethods": [
      {
        "method": "VENMO",
        "deepLink": "venmo://paycharge?txn=pay&recipients=john-doe&amount=83.33"
      }
    ]
  },
  "channels": ["PUSH", "EMAIL"],
  "priority": "HIGH",
  "actionButtons": [
    {
      "text": "Pay Now",
      "action": "OPEN_PAYMENT",
      "data": {"method": "VENMO"}
    },
    {
      "text": "View Details", 
      "action": "OPEN_EXPENSE",
      "data": {"expenseId": "expense-123"}
    }
  ]
}
```

#### Budget Alert Notification
```bash
POST http://localhost:8006/api/notifications/send
Content-Type: application/json
X-Authenticated-User-ID: system

{
  "type": "BUDGET_ALERT",
  "recipientUserIds": ["user123"],
  "title": "Budget Alert: Food",
  "message": "You've used 80% of your monthly food budget with 9 days remaining",
  "data": {
    "category": "Food",
    "budgetAmount": 500.00,
    "spentAmount": 400.00,
    "remainingAmount": 100.00,
    "percentageUsed": 80.0,
    "daysRemaining": 9
  },
  "channels": ["PUSH", "IN_APP"],
  "priority": "MEDIUM"
}
```

### Get User Notifications

#### Get Recent Notifications
```bash
GET http://localhost:8006/api/notifications/user/user123?limit=20&offset=0
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "notifications": [
    {
      "notificationId": "notif-123",
      "type": "EXPENSE_CREATED",
      "title": "New Expense Added",
      "message": "Jane added a $45 expense for 'Uber to Airport' in Tokyo Trip group",
      "isRead": false,
      "createdAt": "2025-06-22T17:35:00Z",
      "data": {
        "expenseId": "expense-456",
        "groupId": "group-456",
        "amount": 45.00
      },
      "actionButtons": [
        {
          "text": "View Expense",
          "action": "OPEN_EXPENSE"
        }
      ]
    },
    {
      "notificationId": "notif-124",
      "type": "PAYMENT_RECEIVED",
      "title": "Payment Received",
      "message": "Bob paid you $50.00 for Office Lunch expenses",
      "isRead": true,
      "createdAt": "2025-06-22T15:20:00Z",
      "data": {
        "amount": 50.00,
        "fromUserId": "user789",
        "fromUserName": "Bob Wilson"
      }
    }
  ],
  "totalCount": 45,
  "unreadCount": 8,
  "hasMore": true
}
```

#### Mark Notifications as Read
```bash
POST http://localhost:8006/api/notifications/mark-read
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "notificationIds": ["notif-123", "notif-124", "notif-125"]
}
```

**Response:**
```json
{
  "markedAsRead": 3,
  "updatedAt": "2025-06-22T17:40:00Z"
}
```

### Notification Preferences

#### Get User Preferences
```bash
GET http://localhost:8006/api/notifications/preferences/user123
X-Authenticated-User-ID: user123
```

**Response:**
```json
{
  "userId": "user123",
  "preferences": {
    "EXPENSE_CREATED": {
      "enabled": true,
      "channels": ["PUSH", "IN_APP"],
      "quietHours": {
        "enabled": true,
        "startTime": "22:00",
        "endTime": "08:00"
      }
    },
    "PAYMENT_REQUEST": {
      "enabled": true,
      "channels": ["PUSH", "EMAIL", "IN_APP"],
      "immediateDelivery": true
    },
    "PAYMENT_RECEIVED": {
      "enabled": true,
      "channels": ["PUSH", "IN_APP"],
      "quietHours": {
        "enabled": false
      }
    },
    "BUDGET_ALERT": {
      "enabled": true,
      "channels": ["PUSH", "IN_APP"],
      "thresholds": {
        "warning": 75.0,
        "critical": 90.0
      }
    },
    "GROUP_INVITE": {
      "enabled": true,
      "channels": ["PUSH", "EMAIL", "IN_APP"],
      "immediateDelivery": true
    }
  },
  "globalSettings": {
    "timezone": "America/New_York",
    "language": "en",
    "digestFrequency": "DAILY"
  }
}
```

#### Update Notification Preferences
```bash
PUT http://localhost:8006/api/notifications/preferences/user123
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "preferences": {
    "EXPENSE_CREATED": {
      "enabled": true,
      "channels": ["IN_APP"],
      "quietHours": {
        "enabled": true,
        "startTime": "23:00",
        "endTime": "07:00"
      }
    },
    "BUDGET_ALERT": {
      "enabled": true,
      "channels": ["PUSH", "EMAIL"],
      "thresholds": {
        "warning": 70.0,
        "critical": 85.0
      }
    }
  }
}
```

### Bulk Operations

#### Send Group Notification
```bash
POST http://localhost:8006/api/notifications/send-group
Content-Type: application/json
X-Authenticated-User-ID: user123

{
  "groupId": "group-456",
  "excludeUserIds": ["user123"],
  "type": "GROUP_UPDATE",
  "title": "Group Settings Updated",
  "message": "John updated the group name to 'Tokyo Adventure 2025'",
  "data": {
    "groupId": "group-456",
    "updatedBy": "user123",
    "changes": ["groupName"]
  },
  "channels": ["IN_APP"]
}
```

#### Send Digest Notifications
```bash
POST http://localhost:8006/api/notifications/send-digest
Content-Type: application/json
X-Authenticated-User-ID: system

{
  "digestType": "DAILY",
  "recipientUserIds": ["user123", "user456"],
  "includeTypes": ["EXPENSE_CREATED", "PAYMENT_REQUEST", "BUDGET_ALERT"]
}
```

## Event-Driven Processing

### Event Listeners
```java
@Component
public class NotificationEventProcessor {
    
    @EventListener
    @Async
    public void handleExpenseCreated(ExpenseCreatedEvent event) {
        List<String> recipientIds = getGroupMembers(event.getGroupId())
            .stream()
            .filter(id -> !id.equals(event.getCreatedBy()))
            .collect(Collectors.toList());
            
        NotificationRequest notification = NotificationRequest.builder()
            .type(NotificationType.EXPENSE_CREATED)
            .recipientUserIds(recipientIds)
            .title("New Expense Added")
            .message(buildExpenseMessage(event))
            .data(buildExpenseData(event))
            .channels(List.of(Channel.PUSH, Channel.IN_APP))
            .build();
            
        notificationService.sendNotification(notification);
    }
    
    @EventListener
    @Async
    public void handlePaymentReceived(PaymentReceivedEvent event) {
        NotificationRequest notification = NotificationRequest.builder()
            .type(NotificationType.PAYMENT_RECEIVED)
            .recipientUserIds(List.of(event.getToUserId()))
            .title("Payment Received")
            .message(buildPaymentMessage(event))
            .data(buildPaymentData(event))
            .channels(List.of(Channel.PUSH, Channel.IN_APP))
            .priority(Priority.HIGH)
            .build();
            
        notificationService.sendNotification(notification);
    }
    
    @EventListener
    @Async
    public void handleBudgetThresholdExceeded(BudgetAlertEvent event) {
        NotificationRequest notification = NotificationRequest.builder()
            .type(NotificationType.BUDGET_ALERT)
            .recipientUserIds(List.of(event.getUserId()))
            .title("Budget Alert: " + event.getCategory())
            .message(buildBudgetAlertMessage(event))
            .data(buildBudgetAlertData(event))
            .channels(List.of(Channel.PUSH, Channel.IN_APP))
            .priority(determinePriority(event.getPercentageUsed()))
            .build();
            
        notificationService.sendNotification(notification);
    }
}
```

## Multi-Channel Delivery

### Push Notifications
```java
@Service
public class PushNotificationService {
    
    @Autowired
    private FCMService fcmService;
    
    public void sendPushNotification(String userId, NotificationRequest request) {
        UserDevice device = deviceService.getUserDevice(userId);
        
        if (device != null && device.isPushEnabled()) {
            FCMMessage message = FCMMessage.builder()
                .token(device.getFcmToken())
                .title(request.getTitle())
                .body(request.getMessage())
                .data(request.getData())
                .build();
                
            fcmService.send(message);
        }
    }
}
```

### Email Notifications
```java
@Service
public class EmailNotificationService {
    
    @Autowired
    private EmailTemplateService templateService;
    
    public void sendEmailNotification(String userId, NotificationRequest request) {
        User user = userService.getUser(userId);
        
        if (user.isEmailNotificationsEnabled()) {
            EmailTemplate template = templateService.getTemplate(request.getType());
            
            String htmlContent = template.render(
                request.getTitle(),
                request.getMessage(), 
                request.getData()
            );
            
            emailService.send(
                user.getEmail(),
                request.getTitle(),
                htmlContent
            );
        }
    }
}
```

### In-App Notifications
```java
@Service
public class InAppNotificationService {
    
    public void sendInAppNotification(String userId, NotificationRequest request) {
        InAppNotification notification = InAppNotification.builder()
            .userId(userId)
            .type(request.getType())
            .title(request.getTitle())
            .message(request.getMessage())
            .data(request.getData())
            .isRead(false)
            .createdAt(Instant.now())
            .build();
            
        notificationRepository.save(notification);
        
        // Send via WebSocket for real-time delivery
        webSocketService.sendToUser(userId, notification);
    }
}
```

## Template System

### Notification Templates
```java
@Component
public class NotificationTemplateService {
    
    public String renderExpenseCreatedMessage(ExpenseCreatedEvent event) {
        return String.format(
            "%s added a $%.2f expense for '%s' in %s group",
            event.getCreatedByName(),
            event.getAmount(),
            event.getDescription(),
            event.getGroupName()
        );
    }
    
    public String renderPaymentRequestMessage(PaymentRequestEvent event) {
        return String.format(
            "%s is requesting $%.2f for %s expenses",
            event.getRequestedByName(),
            event.getAmount(),
            event.getGroupName()
        );
    }
    
    public String renderBudgetAlertMessage(BudgetAlertEvent event) {
        return String.format(
            "You've used %.0f%% of your monthly %s budget with %d days remaining",
            event.getPercentageUsed(),
            event.getCategory(),
            event.getDaysRemaining()
        );
    }
}
```

### Email Templates
```html
<!-- expense-created-email.html -->
<!DOCTYPE html>
<html>
<head>
    <title>New Expense Added</title>
</head>
<body>
    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
        <h2>New Expense in {{groupName}}</h2>
        
        <div style="background: #f5f5f5; padding: 20px; border-radius: 8px;">
            <h3>{{description}}</h3>
            <p><strong>Amount:</strong> ${{amount}}</p>
            <p><strong>Added by:</strong> {{createdByName}}</p>
            <p><strong>Your share:</strong> ${{userShare}}</p>
        </div>
        
        <div style="margin-top: 20px;">
            <a href="{{appDeepLink}}" style="background: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px;">
                View in App
            </a>
        </div>
    </div>
</body>
</html>
```

## Performance & Scalability

### Async Processing
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean
    public ThreadPoolTaskExecutor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("notification-");
        executor.initialize();
        return executor;
    }
}
```

### Rate Limiting
```java
@Service
public class NotificationRateLimiter {
    
    private final RateLimiter userNotificationLimiter = 
        RateLimiter.create(10.0); // 10 notifications per second per user
    
    public boolean allowNotification(String userId) {
        return userNotificationLimiter.tryAcquire();
    }
}
```

### Batch Processing
```java
@Scheduled(fixedDelay = 30000) // Every 30 seconds
public void processBatchNotifications() {
    List<NotificationRequest> batch = notificationQueue.pollBatch(100);
    
    if (!batch.isEmpty()) {
        // Group by channel for efficient processing
        Map<Channel, List<NotificationRequest>> byChannel = 
            batch.stream().collect(groupingBy(NotificationRequest::getChannel));
            
        // Process each channel in parallel
        byChannel.entrySet().parallelStream().forEach(entry -> {
            Channel channel = entry.getKey();
            List<NotificationRequest> requests = entry.getValue();
            
            switch (channel) {
                case PUSH -> pushService.sendBatch(requests);
                case EMAIL -> emailService.sendBatch(requests);
                case IN_APP -> inAppService.sendBatch(requests);
            }
        });
    }
}
```

## Monitoring & Health

### Health Check
```bash
GET http://localhost:8006/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "notificationQueue": {
      "status": "UP",
      "details": {
        "queueSize": 15,
        "processingRate": "8.5 per second"
      }
    }
  }
}
```

### Metrics
- Notification delivery rates by channel
- User engagement with notifications
- Processing latency and queue sizes
- Failed delivery tracking and retry attempts