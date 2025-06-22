package com.finshare.balance.dto;

import java.math.BigDecimal;

/**
 * DTO representing a user's overall financial position across all groups.
 */
public class OverallBalanceDto {
    
    private BigDecimal netBalance;
    private BigDecimal totalOwedToYou;
    private BigDecimal totalYouOwe;

    // Constructors
    public OverallBalanceDto() {}

    public OverallBalanceDto(BigDecimal netBalance, BigDecimal totalOwedToYou, BigDecimal totalYouOwe) {
        this.netBalance = netBalance;
        this.totalOwedToYou = totalOwedToYou;
        this.totalYouOwe = totalYouOwe;
    }

    // Getters and Setters
    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public BigDecimal getTotalOwedToYou() {
        return totalOwedToYou;
    }

    public void setTotalOwedToYou(BigDecimal totalOwedToYou) {
        this.totalOwedToYou = totalOwedToYou;
    }

    public BigDecimal getTotalYouOwe() {
        return totalYouOwe;
    }

    public void setTotalYouOwe(BigDecimal totalYouOwe) {
        this.totalYouOwe = totalYouOwe;
    }
}