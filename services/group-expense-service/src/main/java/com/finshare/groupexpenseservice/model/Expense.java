package com.finshare.groupexpenseservice.model;

import com.finshare.groupexpenseservice.enums.SplitMethod;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Expense entity representing a financial expense in a group.
 */
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String expenseId;

    @Column(nullable = false)
    private String groupId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitMethod splitMethod;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExpensePayer> paidBy = new ArrayList<>();

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExpenseSplit> splits = new ArrayList<>();

    private boolean isRecurring = false;
    private String recurrenceRule;
    private Instant nextDueDate;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public Expense() {
    }

    /**
     * Constructor for creating a new expense.
     */
    public Expense(String groupId, String description, BigDecimal amount, String category, 
                   SplitMethod splitMethod, String createdBy) {
        this.groupId = groupId;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.splitMethod = splitMethod;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
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
        this.updatedAt = Instant.now();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        this.updatedAt = Instant.now();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = Instant.now();
    }

    public SplitMethod getSplitMethod() {
        return splitMethod;
    }

    public void setSplitMethod(SplitMethod splitMethod) {
        this.splitMethod = splitMethod;
        this.updatedAt = Instant.now();
    }

    public List<ExpensePayer> getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(List<ExpensePayer> paidBy) {
        this.paidBy = paidBy;
        this.updatedAt = Instant.now();
    }

    public List<ExpenseSplit> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplit> splits) {
        this.splits = splits;
        this.updatedAt = Instant.now();
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
        this.updatedAt = Instant.now();
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
        this.updatedAt = Instant.now();
    }

    public Instant getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(Instant nextDueDate) {
        this.nextDueDate = nextDueDate;
        this.updatedAt = Instant.now();
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

    @Override
    public String toString() {
        return "Expense{" +
                "expenseId='" + expenseId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", splitMethod=" + splitMethod +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}