package com.finshare.balance.service;

import com.finshare.balance.dto.GroupBalanceDto;
import com.finshare.balance.dto.OverallBalanceDto;
import com.finshare.balance.entity.Transaction;
import com.finshare.balance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for calculating user balances across groups and overall.
 * 
 * Implements the core balance calculation logic:
 * balance_user = Σ(amount_owed_to_user) - Σ(amount_owed_by_user)
 */
@Service
public class BalanceCalculationService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public BalanceCalculationService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Calculate a user's overall financial position across all groups.
     */
    public OverallBalanceDto calculateOverallBalance(String userId) {
        List<Transaction> userTransactions = transactionRepository.findUnsettledTransactionsByUserId(userId);
        
        BigDecimal totalOwedToUser = BigDecimal.ZERO;
        BigDecimal totalUserOwes = BigDecimal.ZERO;

        for (Transaction transaction : userTransactions) {
            if (transaction.getToUserId().equals(userId)) {
                // Money owed TO this user
                totalOwedToUser = totalOwedToUser.add(transaction.getAmount());
            } else if (transaction.getFromUserId().equals(userId)) {
                // Money this user OWES
                totalUserOwes = totalUserOwes.add(transaction.getAmount());
            }
        }

        BigDecimal netBalance = totalOwedToUser.subtract(totalUserOwes);
        
        return new OverallBalanceDto(netBalance, totalOwedToUser, totalUserOwes);
    }

    /**
     * Calculate balances for all users within a specific group.
     */
    public GroupBalanceDto calculateGroupBalances(String groupId, String requestingUserId) {
        List<Transaction> groupTransactions = transactionRepository.findUnsettledTransactionsByGroupId(groupId);
        
        Map<String, BigDecimal> userBalances = new HashMap<>();
        
        // Initialize balances for all users involved in transactions
        for (Transaction transaction : groupTransactions) {
            userBalances.putIfAbsent(transaction.getFromUserId(), BigDecimal.ZERO);
            userBalances.putIfAbsent(transaction.getToUserId(), BigDecimal.ZERO);
        }
        
        // Calculate net balances
        for (Transaction transaction : groupTransactions) {
            String fromUser = transaction.getFromUserId();
            String toUser = transaction.getToUserId();
            BigDecimal amount = transaction.getAmount();
            
            // fromUser owes money (negative balance impact)
            userBalances.put(fromUser, userBalances.get(fromUser).subtract(amount));
            
            // toUser is owed money (positive balance impact)
            userBalances.put(toUser, userBalances.get(toUser).add(amount));
        }
        
        BigDecimal requestingUserBalance = userBalances.getOrDefault(requestingUserId, BigDecimal.ZERO);
        
        return new GroupBalanceDto(userBalances, requestingUserBalance);
    }

    /**
     * Get the net balance between two specific users.
     */
    public BigDecimal calculateNetBalanceBetweenUsers(String userId1, String userId2) {
        List<Transaction> allTransactions = transactionRepository.findUnsettledTransactionsByUserId(userId1);
        
        BigDecimal netBalance = BigDecimal.ZERO;
        
        for (Transaction transaction : allTransactions) {
            if (transaction.getFromUserId().equals(userId1) && transaction.getToUserId().equals(userId2)) {
                // userId1 owes userId2
                netBalance = netBalance.subtract(transaction.getAmount());
            } else if (transaction.getFromUserId().equals(userId2) && transaction.getToUserId().equals(userId1)) {
                // userId2 owes userId1 (so userId1 is owed)
                netBalance = netBalance.add(transaction.getAmount());
            }
        }
        
        return netBalance;
    }
}