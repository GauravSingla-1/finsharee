package com.finshare.analytics.dto;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardDto(
    Map<String, BigDecimal> spendingByCategory,
    BigDecimal totalSpend
) {}