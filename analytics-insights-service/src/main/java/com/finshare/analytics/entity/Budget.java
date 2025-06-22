package com.finshare.analytics.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "budgets")
public class Budget {
    
    @Id
    @Column(name = "budget_id")
    private String budgetId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "category", nullable = false)
    private String category;
    
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false)
    private BudgetPeriod period;
    
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
    
    public enum BudgetPeriod {
        MONTHLY, WEEKLY
    }
    
    // Constructors
    public Budget() {}
    
    public Budget(String budgetId, String userId, String category, BigDecimal amount, BudgetPeriod period) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.period = period;
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }
    
    // Getters and Setters
    public String getBudgetId() { return budgetId; }
    public void setBudgetId(String budgetId) { this.budgetId = budgetId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BudgetPeriod getPeriod() { return period; }
    public void setPeriod(BudgetPeriod period) { this.period = period; }
    
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    
    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }
}