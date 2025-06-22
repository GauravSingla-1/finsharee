package com.finshare.groupexpenseservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity representing who paid for an expense and how much.
 * An expense can be paid by multiple people.
 */
@Entity
@Table(name = "expense_payers")
public class ExpensePayer {

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

    /**
     * Default constructor required by JPA.
     */
    public ExpensePayer() {
    }

    /**
     * Constructor for creating a new expense payer.
     */
    public ExpensePayer(Expense expense, String userId, BigDecimal amount) {
        this.expense = expense;
        this.userId = userId;
        this.amount = amount;
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

    @Override
    public String toString() {
        return "ExpensePayer{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                '}';
    }
}