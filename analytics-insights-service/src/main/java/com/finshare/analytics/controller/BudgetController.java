package com.finshare.analytics.controller;

import com.finshare.analytics.dto.BudgetDto;
import com.finshare.analytics.dto.CreateBudgetDto;
import com.finshare.analytics.dto.UpdateBudgetDto;
import com.finshare.analytics.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetDto> createBudget(
            @RequestHeader("X-Authenticated-User-ID") String userId,
            @Valid @RequestBody CreateBudgetDto createBudgetDto) {

        BudgetDto budget = budgetService.createBudget(userId, createBudgetDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }

    @GetMapping
    public ResponseEntity<List<BudgetDto>> getBudgets(
            @RequestHeader("X-Authenticated-User-ID") String userId) {

        List<BudgetDto> budgets = budgetService.getUserBudgets(userId);
        return ResponseEntity.ok(budgets);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetDto> updateBudget(
            @RequestHeader("X-Authenticated-User-ID") String userId,
            @PathVariable String budgetId,
            @Valid @RequestBody UpdateBudgetDto updateBudgetDto) {

        Optional<BudgetDto> updatedBudget = budgetService.updateBudget(userId, budgetId, updateBudgetDto);
        return updatedBudget.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @RequestHeader("X-Authenticated-User-ID") String userId,
            @PathVariable String budgetId) {

        boolean deleted = budgetService.deleteBudget(userId, budgetId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}