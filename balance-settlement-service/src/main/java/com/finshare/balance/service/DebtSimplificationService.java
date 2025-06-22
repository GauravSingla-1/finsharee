package com.finshare.balance.service;

import com.finshare.balance.dto.GroupBalanceDto;
import com.finshare.balance.dto.SimplifiedDebtsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Service implementing the debt simplification algorithm.
 * 
 * Transforms a complex web of IOUs into the minimum number of payments required
 * to settle all balances within a group using a greedy algorithm approach.
 */
@Service
public class DebtSimplificationService {

    private final BalanceCalculationService balanceCalculationService;

    @Autowired
    public DebtSimplificationService(BalanceCalculationService balanceCalculationService) {
        this.balanceCalculationService = balanceCalculationService;
    }

    /**
     * Simplify debts within a group to minimum number of transactions.
     * 
     * Algorithm:
     * 1. Calculate net balances for all users
     * 2. Partition users into creditors (positive balance) and debtors (negative balance)
     * 3. Use greedy algorithm to match debtors with creditors
     * 4. Generate minimum payment instructions
     */
    public SimplifiedDebtsDto simplifyGroupDebts(String groupId, String requestingUserId) {
        // Step 1: Get current balances for the group
        GroupBalanceDto groupBalances = balanceCalculationService.calculateGroupBalances(groupId, requestingUserId);
        Map<String, BigDecimal> userBalances = groupBalances.getUserBalances();
        
        // Step 2: Partition users into creditors and debtors
        List<UserBalance> creditors = new ArrayList<>();
        List<UserBalance> debtors = new ArrayList<>();
        
        for (Map.Entry<String, BigDecimal> entry : userBalances.entrySet()) {
            String userId = entry.getKey();
            BigDecimal balance = entry.getValue();
            
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new UserBalance(userId, balance));
            } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new UserBalance(userId, balance.abs())); // Convert to positive for easier calculation
            }
            // Users with zero balance don't need any transactions
        }
        
        // Step 3: Generate minimum transactions using greedy algorithm
        List<SimplifiedDebtsDto.PaymentInstruction> payments = generateMinimumTransactions(debtors, creditors);
        
        return new SimplifiedDebtsDto(payments);
    }

    /**
     * Generate minimum payment instructions using greedy algorithm.
     */
    private List<SimplifiedDebtsDto.PaymentInstruction> generateMinimumTransactions(
            List<UserBalance> debtors, List<UserBalance> creditors) {
        
        List<SimplifiedDebtsDto.PaymentInstruction> payments = new ArrayList<>();
        
        // Sort for consistent results (largest amounts first for efficiency)
        debtors.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
        creditors.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
        
        int debtorIndex = 0;
        int creditorIndex = 0;
        
        while (debtorIndex < debtors.size() && creditorIndex < creditors.size()) {
            UserBalance debtor = debtors.get(debtorIndex);
            UserBalance creditor = creditors.get(creditorIndex);
            
            // Determine payment amount (minimum of what debtor owes and creditor is owed)
            BigDecimal paymentAmount = debtor.getAmount().min(creditor.getAmount());
            
            // Create payment instruction
            payments.add(new SimplifiedDebtsDto.PaymentInstruction(
                debtor.getUserId(),
                creditor.getUserId(),
                paymentAmount
            ));
            
            // Update remaining amounts
            debtor.setAmount(debtor.getAmount().subtract(paymentAmount));
            creditor.setAmount(creditor.getAmount().subtract(paymentAmount));
            
            // Move to next debtor/creditor if current one is settled
            if (debtor.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                debtorIndex++;
            }
            if (creditor.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                creditorIndex++;
            }
        }
        
        return payments;
    }

    /**
     * Helper class to track user balance information during algorithm execution.
     */
    private static class UserBalance {
        private String userId;
        private BigDecimal amount;

        public UserBalance(String userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public String getUserId() {
            return userId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}