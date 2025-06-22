package com.finshare.analytics.dto;

import java.math.BigDecimal;

public record BudgetDto(
    String budgetId,
    String category,
    BigDecimal amount,
    String period,
    BigDecimal currentSpending
) {}