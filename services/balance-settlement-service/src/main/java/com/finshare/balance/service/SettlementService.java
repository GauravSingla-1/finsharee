package com.finshare.balance.service;

import com.finshare.balance.dto.RecordPaymentDto;
import com.finshare.balance.entity.Transaction;
import com.finshare.balance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling payment settlements and recording manual payments.
 * 
 * Provides functionality to:
 * - Record manual payments made outside the app
 * - Mark related transactions as settled
 * - Generate payment deep links for external apps
 */
@Service
public class SettlementService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public SettlementService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Record a manual payment made outside the app and settle corresponding debts.
     * 
     * This method:
     * 1. Creates a settlement transaction record
     * 2. Marks existing debt transactions as settled (up to the payment amount)
     * 3. Ensures transactional integrity
     */
    @Transactional
    public void recordPayment(String payerId, RecordPaymentDto paymentDto) {
        String groupId = paymentDto.getGroupId();
        String recipientId = paymentDto.getToUserId();
        BigDecimal paymentAmount = paymentDto.getAmount();
        
        // Create settlement transaction record
        Transaction settlementTransaction = new Transaction(
            UUID.randomUUID().toString(),
            groupId,
            "SETTLEMENT", // Special expense ID for manual settlements
            payerId,
            recipientId,
            paymentAmount
        );
        settlementTransaction.setIsSettled(true);
        settlementTransaction.setSettledAt(Instant.now());
        
        transactionRepository.save(settlementTransaction);
        
        // Find and settle existing debt transactions between these users
        settleExistingDebts(groupId, payerId, recipientId, paymentAmount);
    }

    /**
     * Settle existing debt transactions between two users up to the payment amount.
     */
    private void settleExistingDebts(String groupId, String payerId, String recipientId, BigDecimal paymentAmount) {
        // Find unsettled transactions where payer owes recipient
        List<Transaction> debtTransactions = transactionRepository
            .findUnsettledTransactionsBetweenUsers(groupId, payerId, recipientId);
        
        BigDecimal remainingPayment = paymentAmount;
        
        for (Transaction debt : debtTransactions) {
            if (remainingPayment.compareTo(BigDecimal.ZERO) <= 0) {
                break; // Payment fully allocated
            }
            
            if (remainingPayment.compareTo(debt.getAmount()) >= 0) {
                // Payment covers this entire debt
                debt.setIsSettled(true);
                debt.setSettledAt(Instant.now());
                remainingPayment = remainingPayment.subtract(debt.getAmount());
                transactionRepository.save(debt);
            } else {
                // Payment only partially covers this debt
                // Create a new transaction for the settled portion
                Transaction partialSettlement = new Transaction(
                    UUID.randomUUID().toString(),
                    debt.getGroupId(),
                    debt.getExpenseId(),
                    debt.getFromUserId(),
                    debt.getToUserId(),
                    remainingPayment
                );
                partialSettlement.setIsSettled(true);
                partialSettlement.setSettledAt(Instant.now());
                transactionRepository.save(partialSettlement);
                
                // Update original debt with remaining amount
                debt.setAmount(debt.getAmount().subtract(remainingPayment));
                transactionRepository.save(debt);
                
                remainingPayment = BigDecimal.ZERO;
            }
        }
    }

    /**
     * Generate a deep link for popular payment apps.
     * 
     * This creates platform-specific URLs that pre-fill payment information
     * for seamless settlement experience.
     */
    public String generatePaymentDeepLink(String paymentApp, String recipientInfo, BigDecimal amount, String description) {
        switch (paymentApp.toLowerCase()) {
            case "gpay":
            case "googlepay":
                return String.format("gpay://pay?pa=%s&pn=%s&am=%.2f&cu=USD&tn=%s",
                    recipientInfo, "FinShare User", amount, description != null ? description : "FinShare Settlement");
                    
            case "paypal":
                return String.format("paypal://paypalme/%s/%.2f",
                    recipientInfo, amount);
                    
            case "venmo":
                return String.format("venmo://pay?txn=pay&recipients=%s&amount=%.2f&note=%s",
                    recipientInfo, amount, description != null ? description : "FinShare Settlement");
                    
            case "cashapp":
                return String.format("cashapp://pay/%s/%.2f",
                    recipientInfo, amount);
                    
            default:
                // Generic payment URL or fallback
                return String.format("https://finshare.app/pay?to=%s&amount=%.2f&note=%s",
                    recipientInfo, amount, description != null ? description : "Settlement");
        }
    }

    /**
     * Get settlement history for a user.
     */
    public List<Transaction> getSettlementHistory(String userId) {
        return transactionRepository.findUnsettledTransactionsByUserId(userId)
            .stream()
            .filter(t -> t.getExpenseId().equals("SETTLEMENT"))
            .toList();
    }
}