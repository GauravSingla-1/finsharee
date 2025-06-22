package com.finshare.balance.controller;

import com.finshare.balance.dto.GroupBalanceDto;
import com.finshare.balance.dto.OverallBalanceDto;
import com.finshare.balance.dto.SimplifiedDebtsDto;
import com.finshare.balance.service.BalanceCalculationService;
import com.finshare.balance.service.DebtSimplificationService;
import com.finshare.balance.service.TransactionSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for balance-related operations.
 * 
 * Provides endpoints for:
 * - Overall user balance calculations
 * - Group-specific balance queries
 * - Debt simplification algorithms
 */
@RestController
@RequestMapping("/api")
public class BalanceController {

    private final BalanceCalculationService balanceCalculationService;
    private final DebtSimplificationService debtSimplificationService;
    private final TransactionSyncService transactionSyncService;

    @Autowired
    public BalanceController(BalanceCalculationService balanceCalculationService,
                           DebtSimplificationService debtSimplificationService,
                           TransactionSyncService transactionSyncService) {
        this.balanceCalculationService = balanceCalculationService;
        this.debtSimplificationService = debtSimplificationService;
        this.transactionSyncService = transactionSyncService;
    }

    /**
     * Get overall balance for the authenticated user across all groups.
     * 
     * @param userId The authenticated user ID (injected by API Gateway)
     * @return Overall balance summary including net balance, total owed to user, and total user owes
     */
    @GetMapping("/balances/me")
    public ResponseEntity<OverallBalanceDto> getOverallBalance(
            @RequestHeader("X-Authenticated-User-ID") String userId) {
        
        OverallBalanceDto balance = balanceCalculationService.calculateOverallBalance(userId);
        return ResponseEntity.ok(balance);
    }

    /**
     * Get balance information for all users within a specific group.
     * 
     * @param groupId The ID of the group to query
     * @param userId The authenticated user ID (injected by API Gateway)
     * @return Balance information for all group members
     */
    @GetMapping("/groups/{groupId}/balances")
    public ResponseEntity<GroupBalanceDto> getGroupBalances(
            @PathVariable String groupId,
            @RequestHeader("X-Authenticated-User-ID") String userId) {
        
        // Auto-sync transactions for this group to ensure we have latest data
        transactionSyncService.syncGroupTransactions(groupId);
        
        GroupBalanceDto balances = balanceCalculationService.calculateGroupBalances(groupId, userId);
        return ResponseEntity.ok(balances);
    }

    /**
     * Get simplified debt settlement plan for a group.
     * 
     * Applies the debt simplification algorithm to reduce complex IOUs
     * into the minimum number of payments required.
     * 
     * @param groupId The ID of the group to simplify debts for
     * @param userId The authenticated user ID (injected by API Gateway)
     * @return Optimized payment plan with minimum transactions
     */
    @GetMapping("/groups/{groupId}/simplified-debts")
    public ResponseEntity<SimplifiedDebtsDto> getSimplifiedDebts(
            @PathVariable String groupId,
            @RequestHeader("X-Authenticated-User-ID") String userId) {
        
        // Ensure we have test data for demonstration
        transactionSyncService.createTestTransactions(groupId, "dev-user-644", "dev-user-545");
        
        SimplifiedDebtsDto simplifiedDebts = debtSimplificationService.simplifyGroupDebts(groupId, userId);
        return ResponseEntity.ok(simplifiedDebts);
    }
}