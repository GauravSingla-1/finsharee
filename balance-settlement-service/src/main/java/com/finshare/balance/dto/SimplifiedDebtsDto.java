package com.finshare.balance.dto;

import java.util.List;

/**
 * DTO representing the optimized payment plan for a group after debt simplification.
 */
public class SimplifiedDebtsDto {
    
    private List<PaymentInstruction> payments;

    // Constructors
    public SimplifiedDebtsDto() {}

    public SimplifiedDebtsDto(List<PaymentInstruction> payments) {
        this.payments = payments;
    }

    // Getters and Setters
    public List<PaymentInstruction> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentInstruction> payments) {
        this.payments = payments;
    }

    /**
     * Inner class representing a single payment instruction in the simplified debt plan.
     */
    public static class PaymentInstruction {
        private String fromUserId;
        private String toUserId;
        private java.math.BigDecimal amount;

        // Constructors
        public PaymentInstruction() {}

        public PaymentInstruction(String fromUserId, String toUserId, java.math.BigDecimal amount) {
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.amount = amount;
        }

        // Getters and Setters
        public String getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(String fromUserId) {
            this.fromUserId = fromUserId;
        }

        public String getToUserId() {
            return toUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
        }

        public java.math.BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }
    }
}