package com.finshare.groupexpenseservice.enums;

/**
 * Enumeration of different methods for splitting expenses.
 */
public enum SplitMethod {
    EQUAL,      // Split equally among all participants
    EXACT,      // Split by exact amounts specified for each participant
    PERCENTAGE, // Split by percentage specified for each participant
    SHARES      // Split by shares/weights specified for each participant
}