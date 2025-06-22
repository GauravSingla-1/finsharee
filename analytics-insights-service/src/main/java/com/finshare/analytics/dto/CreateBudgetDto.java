package com.finshare.analytics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateBudgetDto(
    @NotBlank String category,
    @NotNull @Positive BigDecimal amount,
    @NotBlank String period
) {}