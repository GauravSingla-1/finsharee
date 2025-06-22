package com.finshare.groupexpenseservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity representing how an expense is split among group members.
 * Each split represents one person's share of the expense.
 */
@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 5, scale = 2)
    private BigDecimal percentage;

    private Integer shares;

    /**
     * Default constructor required by JPA.
     */
    public ExpenseSplit() {
    }

    /**
     * Constructor for creating a new expense split.
     */
    public ExpenseSplit(Expense expense, String userId, BigDecimal amount) {
        this.expense = expense;
        this.userId = userId;
        this.amount = amount;
    }

    /**
     * Constructor with percentage.
     */
    public ExpenseSplit(Expense expense, String userId, BigDecimal amount, BigDecimal percentage) {
        this.expense = expense;
        this.userId = userId;
        this.amount = amount;
        this.percentage = percentage;
    }

    /**
     * Constructor with shares.
     */
    public ExpenseSplit(Expense expense, String userId, BigDecimal amount, Integer shares) {
        this.expense = expense;
        this.userId = userId;
        this.amount = amount;
        this.shares = shares;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
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

    @Override
    public String toString() {
        return "ExpenseSplit{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", percentage=" + percentage +
                ", shares=" + shares +
                '}';
    }
}