package com.finshare.groupexpenseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the FinShare Group & Expense Service.
 * This service manages groups, expenses, and financial calculations with transactional integrity.
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class GroupExpenseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroupExpenseServiceApplication.class, args);
    }
}