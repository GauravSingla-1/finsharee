package com.finshare.groupexpenseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finshare.groupexpenseservice.enums.SplitMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for creating a new expense.
 */
public class CreateExpenseDto {

    @JsonProperty("description")
    @NotBlank(message = "Description is required")
    private String description;

    @JsonProperty("amount")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @JsonProperty("category")
    private String category;

    @JsonProperty("paidBy")
    @NotNull(message = "PaidBy information is required")
    private List<PayerDto> paidBy;

    @JsonProperty("split")
    @NotNull(message = "Split information is required")
    private SplitDto split;

    @JsonProperty("isRecurring")
    private boolean isRecurring = false;

    @JsonProperty("recurrenceRule")
    private String recurrenceRule;

    /**
     * Default constructor.
     */
    public CreateExpenseDto() {
    }

    /**
     * Constructor with all fields.
     */
    public CreateExpenseDto(String description, BigDecimal amount, String category, 
                           List<PayerDto> paidBy, SplitDto split, boolean isRecurring, String recurrenceRule) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.paidBy = paidBy;
        this.split = split;
        this.isRecurring = isRecurring;
        this.recurrenceRule = recurrenceRule;
    }

    // Getters and setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<PayerDto> getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(List<PayerDto> paidBy) {
        this.paidBy = paidBy;
    }

    public SplitDto getSplit() {
        return split;
    }

    public void setSplit(SplitDto split) {
        this.split = split;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    /**
     * DTO for payer information.
     */
    public static class PayerDto {
        @JsonProperty("userId")
        @NotBlank(message = "User ID is required")
        private String userId;

        @JsonProperty("amount")
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;

        public PayerDto() {}

        public PayerDto(String userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    /**
     * DTO for split information.
     */
    public static class SplitDto {
        @JsonProperty("method")
        @NotNull(message = "Split method is required")
        private SplitMethod method;

        @JsonProperty("details")
        private Map<String, Object> details;

        public SplitDto() {}

        public SplitDto(SplitMethod method, Map<String, Object> details) {
            this.method = method;
            this.details = details;
        }

        public SplitMethod getMethod() {
            return method;
        }

        public void setMethod(SplitMethod method) {
            this.method = method;
        }

        public Map<String, Object> getDetails() {
            return details;
        }

        public void setDetails(Map<String, Object> details) {
            this.details = details;
        }
    }
}