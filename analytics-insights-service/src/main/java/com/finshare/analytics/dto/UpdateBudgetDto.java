package com.finshare.analytics.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record UpdateBudgetDto(
    @NotNull @Positive BigDecimal amount
) {}