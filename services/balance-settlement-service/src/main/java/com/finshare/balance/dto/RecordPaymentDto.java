package com.finshare.balance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO for recording a manual payment made outside the app.
 */
public class RecordPaymentDto {
    
    @NotBlank(message = "Group ID is required")
    private String groupId;
    
    @NotBlank(message = "Recipient user ID is required")
    private String toUserId;
    
    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be positive")
    private BigDecimal amount;

    private String description;

    // Constructors
    public RecordPaymentDto() {}

    public RecordPaymentDto(String groupId, String toUserId, BigDecimal amount) {
        this.groupId = groupId;
        this.toUserId = toUserId;
        this.amount = amount;
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}