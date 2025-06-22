package com.finshare.groupexpenseservice.service;

import com.finshare.groupexpenseservice.dto.CreateExpenseDto;
import com.finshare.groupexpenseservice.dto.ExpenseDto;
import com.finshare.groupexpenseservice.dto.UpdateExpenseDto;
import com.finshare.groupexpenseservice.exception.ExpenseNotFoundException;
import com.finshare.groupexpenseservice.exception.GroupNotFoundException;
import com.finshare.groupexpenseservice.exception.UnauthorizedAccessException;
import com.finshare.groupexpenseservice.mapper.ExpenseMapper;
import com.finshare.groupexpenseservice.model.*;
import com.finshare.groupexpenseservice.repository.ExpenseRepository;
import com.finshare.groupexpenseservice.repository.GroupRepository;
import com.finshare.groupexpenseservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing expense operations with transactional integrity.
 */
@Service
@Transactional
public class ExpenseService {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ExpenseSplitCalculator splitCalculator;

    @Autowired
    private ExpenseMapper expenseMapper;

    /**
     * Create a new expense with transactional integrity.
     *
     * @param groupId The group ID
     * @param createExpenseDto The expense creation data
     * @param authenticatedUserId The authenticated user's ID
     * @return ExpenseDto containing the created expense information
     */
    public ExpenseDto createExpense(String groupId, CreateExpenseDto createExpenseDto, String authenticatedUserId) {
        logger.debug("Creating expense '{}' in group: {} by user: {}", 
                    createExpenseDto.getDescription(), groupId, authenticatedUserId);
        
        // Verify group exists and user is a member
        Group group = findGroupById(groupId);
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        // Create expense entity
        Expense expense = new Expense(
                groupId,
                createExpenseDto.getDescription(),
                createExpenseDto.getAmount(),
                createExpenseDto.getCategory(),
                createExpenseDto.getSplit().getMethod(),
                authenticatedUserId
        );
        
        expense.setRecurring(createExpenseDto.isRecurring());
        expense.setRecurrenceRule(createExpenseDto.getRecurrenceRule());
        
        // Save expense first to get ID
        Expense savedExpense = expenseRepository.save(expense);
        
        // Create payers
        List<ExpensePayer> payers = new ArrayList<>();
        for (CreateExpenseDto.PayerDto payerDto : createExpenseDto.getPaidBy()) {
            ExpensePayer payer = new ExpensePayer(savedExpense, payerDto.getUserId(), payerDto.getAmount());
            payers.add(payer);
        }
        savedExpense.setPaidBy(payers);
        
        // Calculate splits
        List<ExpenseSplit> splits = splitCalculator.calculateSplits(
                createExpenseDto.getAmount(),
                createExpenseDto.getSplit().getMethod(),
                createExpenseDto.getSplit().getDetails(),
                group.getMemberIds()
        );
        
        // Set expense reference for splits
        for (ExpenseSplit split : splits) {
            split.setExpense(savedExpense);
        }
        savedExpense.setSplits(splits);
        
        // Save updated expense with payers and splits
        savedExpense = expenseRepository.save(savedExpense);
        
        // Create transactions (debt relationships)
        List<Transaction> transactions = calculateTransactions(savedExpense);
        transactionRepository.saveAll(transactions);
        
        logger.info("Successfully created expense '{}' with {} splits and {} transactions", 
                   savedExpense.getDescription(), splits.size(), transactions.size());
        
        return expenseMapper.toExpenseDto(savedExpense);
    }

    /**
     * Update an existing expense with transactional integrity.
     *
     * @param expenseId The expense ID
     * @param updateExpenseDto The expense update data
     * @param authenticatedUserId The authenticated user's ID
     * @return ExpenseDto containing the updated expense information
     */
    public ExpenseDto updateExpense(String expenseId, UpdateExpenseDto updateExpenseDto, String authenticatedUserId) {
        logger.debug("Updating expense: {} by user: {}", expenseId, authenticatedUserId);
        
        Expense expense = findExpenseById(expenseId);
        
        // Verify user is a member of the group
        Group group = findGroupById(expense.getGroupId());
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        if (!updateExpenseDto.hasUpdates()) {
            logger.debug("No updates provided for expense: {}", expenseId);
            return expenseMapper.toExpenseDto(expense);
        }
        
        // Delete existing transactions
        transactionRepository.deleteByExpenseId(expenseId);
        
        // Update expense fields
        if (updateExpenseDto.getDescription() != null) {
            expense.setDescription(updateExpenseDto.getDescription());
        }
        if (updateExpenseDto.getAmount() != null) {
            expense.setAmount(updateExpenseDto.getAmount());
        }
        if (updateExpenseDto.getCategory() != null) {
            expense.setCategory(updateExpenseDto.getCategory());
        }
        if (updateExpenseDto.getRecurring() != null) {
            expense.setRecurring(updateExpenseDto.getRecurring());
        }
        if (updateExpenseDto.getRecurrenceRule() != null) {
            expense.setRecurrenceRule(updateExpenseDto.getRecurrenceRule());
        }
        
        // Update payers if provided
        if (updateExpenseDto.getPaidBy() != null) {
            List<ExpensePayer> payers = new ArrayList<>();
            for (CreateExpenseDto.PayerDto payerDto : updateExpenseDto.getPaidBy()) {
                ExpensePayer payer = new ExpensePayer(expense, payerDto.getUserId(), payerDto.getAmount());
                payers.add(payer);
            }
            expense.setPaidBy(payers);
        }
        
        // Update splits if provided
        if (updateExpenseDto.getSplit() != null) {
            expense.setSplitMethod(updateExpenseDto.getSplit().getMethod());
            
            List<ExpenseSplit> splits = splitCalculator.calculateSplits(
                    expense.getAmount(),
                    updateExpenseDto.getSplit().getMethod(),
                    updateExpenseDto.getSplit().getDetails(),
                    group.getMemberIds()
            );
            
            for (ExpenseSplit split : splits) {
                split.setExpense(expense);
            }
            expense.setSplits(splits);
        }
        
        // Save updated expense
        Expense savedExpense = expenseRepository.save(expense);
        
        // Recalculate and create new transactions
        List<Transaction> transactions = calculateTransactions(savedExpense);
        transactionRepository.saveAll(transactions);
        
        logger.info("Successfully updated expense: {}", expenseId);
        return expenseMapper.toExpenseDto(savedExpense);
    }

    /**
     * Delete an expense and all associated transactions.
     *
     * @param expenseId The expense ID
     * @param authenticatedUserId The authenticated user's ID
     */
    public void deleteExpense(String expenseId, String authenticatedUserId) {
        logger.debug("Deleting expense: {} by user: {}", expenseId, authenticatedUserId);
        
        Expense expense = findExpenseById(expenseId);
        
        // Verify user is a member of the group
        Group group = findGroupById(expense.getGroupId());
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        // Delete associated transactions
        transactionRepository.deleteByExpenseId(expenseId);
        
        // Delete expense
        expenseRepository.delete(expense);
        
        logger.info("Successfully deleted expense: {}", expenseId);
    }

    /**
     * Get all expenses for a group.
     *
     * @param groupId The group ID
     * @param authenticatedUserId The authenticated user's ID
     * @return List of expenses for the group
     */
    public List<ExpenseDto> getGroupExpenses(String groupId, String authenticatedUserId) {
        logger.debug("Getting expenses for group: {} by user: {}", groupId, authenticatedUserId);
        
        Group group = findGroupById(groupId);
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        List<Expense> expenses = expenseRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
        logger.debug("Found {} expenses for group: {}", expenses.size(), groupId);
        
        return expenses.stream()
                .map(expenseMapper::toExpenseDto)
                .toList();
    }

    /**
     * Get expense by ID.
     *
     * @param expenseId The expense ID
     * @param authenticatedUserId The authenticated user's ID
     * @return ExpenseDto containing the expense information
     */
    public ExpenseDto getExpense(String expenseId, String authenticatedUserId) {
        logger.debug("Getting expense: {} by user: {}", expenseId, authenticatedUserId);
        
        Expense expense = findExpenseById(expenseId);
        
        // Verify user is a member of the group
        Group group = findGroupById(expense.getGroupId());
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        return expenseMapper.toExpenseDto(expense);
    }

    /**
     * Calculate transactions (debt relationships) from an expense.
     */
    private List<Transaction> calculateTransactions(Expense expense) {
        List<Transaction> transactions = new ArrayList<>();
        
        // For each split, create transactions from split participants to payers
        for (ExpenseSplit split : expense.getSplits()) {
            String owingUserId = split.getUserId();
            BigDecimal owedAmount = split.getAmount();
            
            // Distribute the owed amount among payers proportionally
            BigDecimal totalPaid = expense.getPaidBy().stream()
                    .map(ExpensePayer::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            for (ExpensePayer payer : expense.getPaidBy()) {
                if (!payer.getUserId().equals(owingUserId)) {
                    // Calculate proportional amount owed to this payer
                    BigDecimal payerProportion = payer.getAmount().divide(totalPaid, 4, BigDecimal.ROUND_HALF_UP);
                    BigDecimal amountOwedToPayer = owedAmount.multiply(payerProportion)
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
                    
                    if (amountOwedToPayer.compareTo(BigDecimal.ZERO) > 0) {
                        Transaction transaction = new Transaction(
                                expense.getExpenseId(),
                                expense.getGroupId(),
                                owingUserId,
                                payer.getUserId(),
                                amountOwedToPayer
                        );
                        transactions.add(transaction);
                    }
                }
            }
        }
        
        return transactions;
    }

    /**
     * Find group by ID or throw exception if not found.
     */
    private Group findGroupById(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));
    }

    /**
     * Find expense by ID or throw exception if not found.
     */
    private Expense findExpenseById(String expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with ID: " + expenseId));
    }
}