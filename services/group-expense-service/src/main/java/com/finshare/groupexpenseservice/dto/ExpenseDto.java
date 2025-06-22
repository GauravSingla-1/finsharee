package com.finshare.groupexpenseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finshare.groupexpenseservice.enums.SplitMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Data Transfer Object for expense information.
 */
public class ExpenseDto {

    @JsonProperty("expenseId")
    private String expenseId;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("category")
    private String category;

    @JsonProperty("splitMethod")
    private SplitMethod splitMethod;

    @JsonProperty("paidBy")
    private List<PayerDto> paidBy;

    @JsonProperty("splits")
    private List<SplitDetailDto> splits;

    @JsonProperty("isRecurring")
    private boolean isRecurring;

    @JsonProperty("recurrenceRule")
    private String recurrenceRule;

    @JsonProperty("nextDueDate")
    private Instant nextDueDate;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("updatedAt")
    private Instant updatedAt;

    /**
     * Default constructor.
     */
    public ExpenseDto() {
    }

    // Getters and setters

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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

    public SplitMethod getSplitMethod() {
        return splitMethod;
    }

    public void setSplitMethod(SplitMethod splitMethod) {
        this.splitMethod = splitMethod;
    }

    public List<PayerDto> getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(List<PayerDto> paidBy) {
        this.paidBy = paidBy;
    }

    public List<SplitDetailDto> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitDetailDto> splits) {
        this.splits = splits;
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

    public Instant getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(Instant nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * DTO for payer information.
     */
    public static class PayerDto {
        @JsonProperty("userId")
        private String userId;

        @JsonProperty("amount")
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
     * DTO for split detail information.
     */
    public static class SplitDetailDto {
        @JsonProperty("userId")
        private String userId;

        @JsonProperty("amount")
        private BigDecimal amount;

        @JsonProperty("percentage")
        private BigDecimal percentage;

        @JsonProperty("shares")
        private Integer shares;

        public SplitDetailDto() {}

        public SplitDetailDto(String userId, BigDecimal amount) {
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

        public BigDecimal getPercentage() {
            return percentage;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }

        public Integer getShares() {
            return shares;
        }

        public void setShares(Integer shares) {
            this.shares = shares;
        }
    }
}