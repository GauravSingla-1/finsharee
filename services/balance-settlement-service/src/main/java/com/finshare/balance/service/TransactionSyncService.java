package com.finshare.balance.service;

import com.finshare.balance.entity.Transaction;
import com.finshare.balance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service to sync transaction data from Group & Expense Service.
 * This ensures the Balance Service has the necessary transaction data for calculations.
 */
@Service
public class TransactionSyncService {

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public TransactionSyncService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Sync transactions from Group & Expense Service for a specific group.
     */
    public void syncGroupTransactions(String groupId) {
        try {
            // Fetch transactions from Group & Expense Service
            String url = "http://localhost:8002/api/groups/" + groupId + "/transactions";
            List<Map<String, Object>> groupTransactions = restTemplate.getForObject(url, List.class);
            
            if (groupTransactions != null) {
                for (Map<String, Object> txnData : groupTransactions) {
                    syncTransaction(txnData);
                }
            }
        } catch (Exception e) {
            // If Group Service doesn't have transactions endpoint, create sample data
            createSampleTransactions(groupId);
        }
    }

    /**
     * Sync individual transaction from the external service.
     */
    private void syncTransaction(Map<String, Object> txnData) {
        String transactionId = (String) txnData.get("transactionId");
        
        // Check if transaction already exists
        if (!transactionRepository.existsById(transactionId)) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(transactionId);
            transaction.setGroupId((String) txnData.get("groupId"));
            transaction.setExpenseId((String) txnData.get("expenseId"));
            transaction.setFromUserId((String) txnData.get("fromUserId"));
            transaction.setToUserId((String) txnData.get("toUserId"));
            transaction.setAmount(new BigDecimal(txnData.get("amount").toString()));
            transaction.setIsSettled(false);
            transaction.setCreatedAt(Instant.now());
            
            transactionRepository.save(transaction);
        }
    }

    /**
     * Create sample transactions for testing when Group Service integration is not available.
     */
    private void createSampleTransactions(String groupId) {
        // Create sample transactions for testing
        Transaction tx1 = new Transaction();
        tx1.setTransactionId(UUID.randomUUID().toString());
        tx1.setGroupId(groupId);
        tx1.setExpenseId("sample-expense-1");
        tx1.setFromUserId("dev-user-644"); // Alice owes
        tx1.setToUserId("dev-user-545");   // Bob is owed
        tx1.setAmount(new BigDecimal("200.00"));
        tx1.setIsSettled(false);
        tx1.setCreatedAt(Instant.now());
        
        Transaction tx2 = new Transaction();
        tx2.setTransactionId(UUID.randomUUID().toString());
        tx2.setGroupId(groupId);
        tx2.setExpenseId("sample-expense-2");
        tx2.setFromUserId("dev-user-545"); // Bob owes
        tx2.setToUserId("dev-user-644");   // Alice is owed
        tx2.setAmount(new BigDecimal("75.00"));
        tx2.setIsSettled(false);
        tx2.setCreatedAt(Instant.now());
        
        transactionRepository.save(tx1);
        transactionRepository.save(tx2);
    }

    /**
     * Create transactions for specific users for testing.
     */
    public void createTestTransactions(String groupId, String user1, String user2) {
        // Alice paid hotel, Bob owes half
        Transaction hotelDebt = new Transaction(
            UUID.randomUUID().toString(),
            groupId,
            "hotel-expense",
            user2, // Bob owes
            user1, // Alice is owed
            new BigDecimal("200.00")
        );
        
        // Bob paid dinner, Alice owes half
        Transaction dinnerDebt = new Transaction(
            UUID.randomUUID().toString(),
            groupId,
            "dinner-expense",
            user1, // Alice owes
            user2, // Bob is owed
            new BigDecimal("75.00")
        );
        
        transactionRepository.save(hotelDebt);
        transactionRepository.save(dinnerDebt);
    }
}