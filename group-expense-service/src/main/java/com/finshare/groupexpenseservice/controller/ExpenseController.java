package com.finshare.groupexpenseservice.controller;

import com.finshare.groupexpenseservice.dto.CreateExpenseDto;
import com.finshare.groupexpenseservice.dto.ExpenseDto;
import com.finshare.groupexpenseservice.dto.UpdateExpenseDto;
import com.finshare.groupexpenseservice.service.ExpenseService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for expense-related operations.
 */
@RestController
@Validated
public class ExpenseController {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseController.class);
    private static final String AUTHENTICATED_USER_HEADER = "X-Authenticated-User-ID";

    @Autowired
    private ExpenseService expenseService;

    /**
     * Create a new expense in a group.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param groupId The group ID
     * @param createExpenseDto The expense creation data
     * @return ResponseEntity containing the created expense information
     */
    @PostMapping("/groups/{groupId}/expenses")
    public ResponseEntity<ExpenseDto> createExpense(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String groupId,
            @Valid @RequestBody CreateExpenseDto createExpenseDto) {
        
        logger.debug("POST /groups/{}/expenses - Creating expense '{}' by user: {}", 
                    groupId, createExpenseDto.getDescription(), authenticatedUserId);
        
        ExpenseDto expenseDto = expenseService.createExpense(groupId, createExpenseDto, authenticatedUserId);
        logger.debug("Successfully created expense: {}", expenseDto.getExpenseId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseDto);
    }

    /**
     * Update an existing expense.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param expenseId The expense ID
     * @param updateExpenseDto The expense update data
     * @return ResponseEntity containing the updated expense information
     */
    @PutMapping("/expenses/{expenseId}")
    public ResponseEntity<ExpenseDto> updateExpense(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String expenseId,
            @Valid @RequestBody UpdateExpenseDto updateExpenseDto) {
        
        logger.debug("PUT /expenses/{} - Updating expense by user: {}", expenseId, authenticatedUserId);
        
        ExpenseDto expenseDto = expenseService.updateExpense(expenseId, updateExpenseDto, authenticatedUserId);
        logger.debug("Successfully updated expense: {}", expenseId);
        
        return ResponseEntity.ok(expenseDto);
    }

    /**
     * Delete an expense.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param expenseId The expense ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/expenses/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String expenseId) {
        
        logger.debug("DELETE /expenses/{} - Deleting expense by user: {}", expenseId, authenticatedUserId);
        
        expenseService.deleteExpense(expenseId, authenticatedUserId);
        logger.debug("Successfully deleted expense: {}", expenseId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all expenses for a group.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param groupId The group ID
     * @return ResponseEntity containing the list of expenses
     */
    @GetMapping("/groups/{groupId}/expenses")
    public ResponseEntity<List<ExpenseDto>> getGroupExpenses(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String groupId) {
        
        logger.debug("GET /groups/{}/expenses - Getting expenses by user: {}", groupId, authenticatedUserId);
        
        List<ExpenseDto> expenses = expenseService.getGroupExpenses(groupId, authenticatedUserId);
        logger.debug("Successfully retrieved {} expenses for group: {}", expenses.size(), groupId);
        
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get expense by ID.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param expenseId The expense ID
     * @return ResponseEntity containing the expense information
     */
    @GetMapping("/expenses/{expenseId}")
    public ResponseEntity<ExpenseDto> getExpense(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String expenseId) {
        
        logger.debug("GET /expenses/{} - Getting expense by user: {}", expenseId, authenticatedUserId);
        
        ExpenseDto expenseDto = expenseService.getExpense(expenseId, authenticatedUserId);
        logger.debug("Successfully retrieved expense: {}", expenseId);
        
        return ResponseEntity.ok(expenseDto);
    }
}