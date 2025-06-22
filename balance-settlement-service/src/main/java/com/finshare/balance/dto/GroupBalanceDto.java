package com.finshare.balance.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO representing balance information for all users within a specific group.
 */
public class GroupBalanceDto {
    
    private Map<String, BigDecimal> userBalances;
    private BigDecimal netBalanceForUser;

    // Constructors
    public GroupBalanceDto() {}

    public GroupBalanceDto(Map<String, BigDecimal> userBalances, BigDecimal netBalanceForUser) {
        this.userBalances = userBalances;
        this.netBalanceForUser = netBalanceForUser;
    }

    // Getters and Setters
    public Map<String, BigDecimal> getUserBalances() {
        return userBalances;
    }

    public void setUserBalances(Map<String, BigDecimal> userBalances) {
        this.userBalances = userBalances;
    }

    public BigDecimal getNetBalanceForUser() {
        return netBalanceForUser;
    }

    public void setNetBalanceForUser(BigDecimal netBalanceForUser) {
        this.netBalanceForUser = netBalanceForUser;
    }
}