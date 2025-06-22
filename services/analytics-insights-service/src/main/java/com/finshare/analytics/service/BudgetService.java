package com.finshare.analytics.service;

import com.finshare.analytics.dto.BudgetDto;
import com.finshare.analytics.dto.CreateBudgetDto;
import com.finshare.analytics.dto.UpdateBudgetDto;
import com.finshare.analytics.entity.Budget;
import com.finshare.analytics.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public BudgetDto createBudget(String userId, CreateBudgetDto createBudgetDto) {
        Budget budget = new Budget(
            UUID.randomUUID().toString(),
            userId,
            createBudgetDto.category(),
            createBudgetDto.amount(),
            Budget.BudgetPeriod.valueOf(createBudgetDto.period().toUpperCase())
        );

        Budget savedBudget = budgetRepository.save(budget);
        return convertToDto(savedBudget);
    }

    public List<BudgetDto> getUserBudgets(String userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public Optional<BudgetDto> updateBudget(String userId, String budgetId, UpdateBudgetDto updateBudgetDto) {
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        
        if (budgetOpt.isPresent() && budgetOpt.get().getUserId().equals(userId)) {
            Budget budget = budgetOpt.get();
            budget.setAmount(updateBudgetDto.amount());
            Budget savedBudget = budgetRepository.save(budget);
            return Optional.of(convertToDto(savedBudget));
        }
        
        return Optional.empty();
    }

    public boolean deleteBudget(String userId, String budgetId) {
        Optional<Budget> budgetOpt = budgetRepository.findById(budgetId);
        
        if (budgetOpt.isPresent() && budgetOpt.get().getUserId().equals(userId)) {
            budgetRepository.deleteById(budgetId);
            return true;
        }
        
        return false;
    }

    private BudgetDto convertToDto(Budget budget) {
        BigDecimal currentSpending = getCurrentSpending(budget.getUserId(), budget.getCategory());
        
        return new BudgetDto(
            budget.getBudgetId(),
            budget.getCategory(),
            budget.getAmount(),
            budget.getPeriod().name(),
            currentSpending
        );
    }

    private BigDecimal getCurrentSpending(String userId, String category) {
        YearMonth currentMonth = YearMonth.now();
        String key = String.format("%s:dashboard:%s", userId, currentMonth.toString());
        String spending = (String) redisTemplate.opsForHash().get(key, category);
        
        return spending != null ? new BigDecimal(spending) : BigDecimal.ZERO;
    }
}