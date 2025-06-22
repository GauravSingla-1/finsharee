package com.finshare.groupexpenseservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Transaction entity representing a debt/credit between two users from an expense.
 * This represents "User A owes User B $X" relationships.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;

    @Column(nullable = false)
    private String expenseId;

    @Column(nullable = false)
    private String groupId;

    @Column(nullable = false)
    private String fromUserId; // Who owes the money

    @Column(nullable = false)
    private String toUserId; // Who is owed the money

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant createdAt;

    /**
     * Default constructor required by JPA.
     */
    public Transaction() {
    }

    /**
     * Constructor for creating a new transaction.
     */
    public Transaction(String expenseId, String groupId, String fromUserId, String toUserId, BigDecimal amount) {
        this.expenseId = expenseId;
        this.groupId = groupId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    // Getters and setters

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", expenseId='" + expenseId + '\'' +
                ", fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", amount=" + amount +
                ", createdAt=" + createdAt +
                '}';
    }
}