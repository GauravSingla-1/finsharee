package com.finshare.balance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the FinShare Balance & Settlement Service.
 * 
 * This service handles:
 * - Real-time balance calculations across groups
 * - Debt simplification algorithms (minimum payment optimization)
 * - Settlement facilitation with payment recording
 * - Integration with external payment systems
 */
@SpringBootApplication
public class BalanceSettlementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BalanceSettlementServiceApplication.class, args);
    }
}