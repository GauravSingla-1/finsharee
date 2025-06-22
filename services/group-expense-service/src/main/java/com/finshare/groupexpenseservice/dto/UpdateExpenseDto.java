package com.finshare.groupexpenseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finshare.groupexpenseservice.enums.SplitMethod;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for updating an expense.
 * All fields are optional for partial updates.
 */
public class UpdateExpenseDto {

    @JsonProperty("description")
    private String description;

    @JsonProperty("amount")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @JsonProperty("category")
    private String category;

    @JsonProperty("paidBy")
    private List<CreateExpenseDto.PayerDto> paidBy;

    @JsonProperty("split")
    private CreateExpenseDto.SplitDto split;

    @JsonProperty("isRecurring")
    private Boolean isRecurring;

    @JsonProperty("recurrenceRule")
    private String recurrenceRule;

    /**
     * Default constructor.
     */
    public UpdateExpenseDto() {
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

    public List<CreateExpenseDto.PayerDto> getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(List<CreateExpenseDto.PayerDto> paidBy) {
        this.paidBy = paidBy;
    }

    public CreateExpenseDto.SplitDto getSplit() {
        return split;
    }

    public void setSplit(CreateExpenseDto.SplitDto split) {
        this.split = split;
    }

    public Boolean getRecurring() {
        return isRecurring;
    }

    public void setRecurring(Boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    /**
     * Check if any field is provided for update.
     */
    public boolean hasUpdates() {
        return description != null || amount != null || category != null || 
               paidBy != null || split != null || isRecurring != null || recurrenceRule != null;
    }
}