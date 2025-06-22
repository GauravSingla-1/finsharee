package com.finshare.groupexpenseservice.repository;

import com.finshare.groupexpenseservice.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository interface for Expense entities.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {

    /**
     * Find all expenses for a specific group.
     *
     * @param groupId The group ID
     * @return List of expenses for the group
     */
    List<Expense> findByGroupIdOrderByCreatedAtDesc(String groupId);

    /**
     * Find all recurring expenses that are due for processing.
     *
     * @param currentDate The current date
     * @return List of recurring expenses that are due
     */
    @Query("SELECT e FROM Expense e WHERE e.isRecurring = true AND e.nextDueDate <= :currentDate")
    List<Expense> findRecurringExpensesDue(@Param("currentDate") Instant currentDate);

    /**
     * Find expenses created by a specific user in a group.
     *
     * @param groupId The group ID
     * @param createdBy The user ID who created the expenses
     * @return List of expenses created by the user in the group
     */
    List<Expense> findByGroupIdAndCreatedByOrderByCreatedAtDesc(String groupId, String createdBy);
}