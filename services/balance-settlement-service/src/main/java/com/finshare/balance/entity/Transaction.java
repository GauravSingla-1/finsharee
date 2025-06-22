package com.finshare.balance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entity representing a financial transaction between users.
 * This mirrors the transaction data created by the Group & Expense Service.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @NotBlank
    @Column(name = "group_id", nullable = false)
    private String groupId;

    @NotBlank
    @Column(name = "expense_id", nullable = false)
    private String expenseId;

    @NotBlank
    @Column(name = "from_user_id", nullable = false)
    private String fromUserId;

    @NotBlank
    @Column(name = "to_user_id", nullable = false)
    private String toUserId;

    @NotNull
    @Positive
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "is_settled", nullable = false)
    private Boolean isSettled = false;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "settled_at")
    private Instant settledAt;

    // Constructors
    public Transaction() {
        this.createdAt = Instant.now();
    }

    public Transaction(String transactionId, String groupId, String expenseId, 
                      String fromUserId, String toUserId, BigDecimal amount) {
        this();
        this.transactionId = transactionId;
        this.groupId = groupId;
        this.expenseId = expenseId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
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

    public Boolean getIsSettled() {
        return isSettled;
    }

    public void setIsSettled(Boolean isSettled) {
        this.isSettled = isSettled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSettledAt() {
        return settledAt;
    }

    public void setSettledAt(Instant settledAt) {
        this.settledAt = settledAt;
    }
}