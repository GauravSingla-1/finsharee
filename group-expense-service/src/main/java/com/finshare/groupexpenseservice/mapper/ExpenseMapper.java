package com.finshare.groupexpenseservice.mapper;

import com.finshare.groupexpenseservice.dto.ExpenseDto;
import com.finshare.groupexpenseservice.model.Expense;
import com.finshare.groupexpenseservice.model.ExpensePayer;
import com.finshare.groupexpenseservice.model.ExpenseSplit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for converting between Expense entities and DTOs.
 */
@Component
public class ExpenseMapper {

    /**
     * Convert Expense entity to ExpenseDto.
     *
     * @param expense The expense entity
     * @return ExpenseDto with expense information
     */
    public ExpenseDto toExpenseDto(Expense expense) {
        if (expense == null) {
            return null;
        }
        
        ExpenseDto dto = new ExpenseDto();
        dto.setExpenseId(expense.getExpenseId());
        dto.setGroupId(expense.getGroupId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setCategory(expense.getCategory());
        dto.setSplitMethod(expense.getSplitMethod());
        dto.setRecurring(expense.isRecurring());
        dto.setRecurrenceRule(expense.getRecurrenceRule());
        dto.setNextDueDate(expense.getNextDueDate());
        dto.setCreatedBy(expense.getCreatedBy());
        dto.setCreatedAt(expense.getCreatedAt());
        dto.setUpdatedAt(expense.getUpdatedAt());
        
        // Map payers
        List<ExpenseDto.PayerDto> payerDtos = expense.getPaidBy().stream()
                .map(this::toPayerDto)
                .collect(Collectors.toList());
        dto.setPaidBy(payerDtos);
        
        // Map splits
        List<ExpenseDto.SplitDetailDto> splitDtos = expense.getSplits().stream()
                .map(this::toSplitDetailDto)
                .collect(Collectors.toList());
        dto.setSplits(splitDtos);
        
        return dto;
    }

    /**
     * Convert ExpensePayer to PayerDto.
     */
    private ExpenseDto.PayerDto toPayerDto(ExpensePayer payer) {
        return new ExpenseDto.PayerDto(payer.getUserId(), payer.getAmount());
    }

    /**
     * Convert ExpenseSplit to SplitDetailDto.
     */
    private ExpenseDto.SplitDetailDto toSplitDetailDto(ExpenseSplit split) {
        ExpenseDto.SplitDetailDto dto = new ExpenseDto.SplitDetailDto(split.getUserId(), split.getAmount());
        dto.setPercentage(split.getPercentage());
        dto.setShares(split.getShares());
        return dto;
    }
}