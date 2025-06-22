package com.finshare.balance.controller;

import com.finshare.balance.dto.RecordPaymentDto;
import com.finshare.balance.entity.Transaction;
import com.finshare.balance.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for settlement-related operations.
 * 
 * Provides endpoints for:
 * - Recording manual payments
 * - Generating payment deep links
 * - Settlement history tracking
 */
@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    @Autowired
    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    /**
     * Record a manual payment made outside the app.
     * 
     * This endpoint allows users to record payments made through cash,
     * bank transfers, or other external payment methods to keep
     * FinShare balances accurate.
     * 
     * @param paymentDto Payment details including group, recipient, and amount
     * @param userId The authenticated user ID (injected by API Gateway)
     * @return 204 No Content on successful recording
     */
    @PostMapping("/record")
    public ResponseEntity<Void> recordPayment(
            @Valid @RequestBody RecordPaymentDto paymentDto,
            @RequestHeader("X-Authenticated-User-ID") String userId) {
        
        settlementService.recordPayment(userId, paymentDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Generate a deep link for external payment apps.
     * 
     * Creates platform-specific URLs that pre-fill payment information
     * for seamless settlement through popular payment apps.
     * 
     * @param app Payment app name (gpay, paypal, venmo, cashapp)
     * @param recipientInfo Recipient identifier (phone, email, username)
     * @param amount Payment amount
     * @param description Optional payment description
     * @return Deep link URL for the specified payment app
     */
    @GetMapping("/payment-link")
    public ResponseEntity<Map<String, String>> generatePaymentLink(
            @RequestParam String app,
            @RequestParam String recipientInfo,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        
        String deepLink = settlementService.generatePaymentDeepLink(app, recipientInfo, amount, description);
        
        return ResponseEntity.ok(Map.of(
            "paymentApp", app,
            "deepLink", deepLink,
            "amount", amount.toString()
        ));
    }

    /**
     * Get settlement history for the authenticated user.
     * 
     * @param userId The authenticated user ID (injected by API Gateway)
     * @return List of settlement transactions
     */
    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getSettlementHistory(
            @RequestHeader("X-Authenticated-User-ID") String userId) {
        
        List<Transaction> history = settlementService.getSettlementHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Health check endpoint for the settlement service.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "balance-settlement",
            "timestamp", java.time.Instant.now().toString()
        ));
    }
}