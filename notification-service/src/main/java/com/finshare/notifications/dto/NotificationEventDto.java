package com.finshare.notifications.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * Base DTO for all notification events consumed from Pub/Sub
 */
public class NotificationEventDto {

    @NotBlank
    @JsonProperty("eventType")
    private String eventType;

    // Common fields for all events
    @JsonProperty("timestamp")
    private String timestamp;

    // New Expense Event fields
    @JsonProperty("expenseId")
    private String expenseId;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("groupName")
    private String groupName;

    @JsonProperty("addedByUserId")
    private String addedByUserId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("description")
    private String description;

    @JsonProperty("involvedUserIds")
    private List<String> involvedUserIds;

    // Payment Recorded Event fields
    @JsonProperty("fromUserId")
    private String fromUserId;

    @JsonProperty("toUserId")
    private String toUserId;

    // Budget Alert Event fields
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("category")
    private String category;

    @JsonProperty("percentage")
    private Integer percentage;

    @JsonProperty("budgetId")
    private String budgetId;

    // Constructors
    public NotificationEventDto() {}

    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getAddedByUserId() { return addedByUserId; }
    public void setAddedByUserId(String addedByUserId) { this.addedByUserId = addedByUserId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getInvolvedUserIds() { return involvedUserIds; }
    public void setInvolvedUserIds(List<String> involvedUserIds) { this.involvedUserIds = involvedUserIds; }

    public String getFromUserId() { return fromUserId; }
    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }

    public String getToUserId() { return toUserId; }
    public void setToUserId(String toUserId) { this.toUserId = toUserId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getPercentage() { return percentage; }
    public void setPercentage(Integer percentage) { this.percentage = percentage; }

    public String getBudgetId() { return budgetId; }
    public void setBudgetId(String budgetId) { this.budgetId = budgetId; }
}