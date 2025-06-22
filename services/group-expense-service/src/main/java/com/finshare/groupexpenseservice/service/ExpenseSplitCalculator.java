package com.finshare.groupexpenseservice.service;

import com.finshare.groupexpenseservice.dto.CreateExpenseDto;
import com.finshare.groupexpenseservice.enums.SplitMethod;
import com.finshare.groupexpenseservice.model.ExpenseSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for calculating expense splits based on different methods.
 */
@Service
public class ExpenseSplitCalculator {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseSplitCalculator.class);

    /**
     * Calculate expense splits based on the split method and details.
     *
     * @param totalAmount The total amount to split
     * @param splitMethod The split method to use
     * @param splitDetails The split details containing user-specific information
     * @param memberIds All member IDs in the group (for equal split)
     * @return List of expense splits
     */
    public List<ExpenseSplit> calculateSplits(BigDecimal totalAmount, SplitMethod splitMethod, 
                                            Map<String, Object> splitDetails, List<String> memberIds) {
        logger.debug("Calculating splits for amount: {} using method: {}", totalAmount, splitMethod);
        
        return switch (splitMethod) {
            case EQUAL -> calculateEqualSplits(totalAmount, memberIds);
            case EXACT -> calculateExactSplits(totalAmount, splitDetails);
            case PERCENTAGE -> calculatePercentageSplits(totalAmount, splitDetails);
            case SHARES -> calculateShareSplits(totalAmount, splitDetails);
        };
    }

    /**
     * Calculate equal splits among all members.
     */
    private List<ExpenseSplit> calculateEqualSplits(BigDecimal totalAmount, List<String> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("Member IDs cannot be empty for equal split");
        }

        List<ExpenseSplit> splits = new ArrayList<>();
        BigDecimal memberCount = BigDecimal.valueOf(memberIds.size());
        BigDecimal splitAmount = totalAmount.divide(memberCount, 2, RoundingMode.HALF_UP);
        
        // Handle remainder due to rounding
        BigDecimal totalAssigned = splitAmount.multiply(memberCount);
        BigDecimal remainder = totalAmount.subtract(totalAssigned);
        
        for (int i = 0; i < memberIds.size(); i++) {
            String userId = memberIds.get(i);
            BigDecimal amount = splitAmount;
            
            // Add remainder to the first member
            if (i == 0) {
                amount = amount.add(remainder);
            }
            
            splits.add(new ExpenseSplit(null, userId, amount));
        }
        
        logger.debug("Calculated {} equal splits of approximately {} each", splits.size(), splitAmount);
        return splits;
    }

    /**
     * Calculate exact splits based on specified amounts.
     */
    @SuppressWarnings("unchecked")
    private List<ExpenseSplit> calculateExactSplits(BigDecimal totalAmount, Map<String, Object> splitDetails) {
        if (splitDetails == null || !splitDetails.containsKey("amounts")) {
            throw new IllegalArgumentException("Exact split requires 'amounts' in split details");
        }

        Map<String, Number> amounts = (Map<String, Number>) splitDetails.get("amounts");
        List<ExpenseSplit> splits = new ArrayList<>();
        BigDecimal totalSpecified = BigDecimal.ZERO;
        
        for (Map.Entry<String, Number> entry : amounts.entrySet()) {
            String userId = entry.getKey();
            BigDecimal amount = new BigDecimal(entry.getValue().toString());
            totalSpecified = totalSpecified.add(amount);
            splits.add(new ExpenseSplit(null, userId, amount));
        }
        
        // Validate that specified amounts equal total amount
        if (totalSpecified.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException(
                String.format("Specified amounts (%s) do not equal total amount (%s)", 
                             totalSpecified, totalAmount));
        }
        
        logger.debug("Calculated {} exact splits totaling {}", splits.size(), totalSpecified);
        return splits;
    }

    /**
     * Calculate percentage-based splits.
     */
    @SuppressWarnings("unchecked")
    private List<ExpenseSplit> calculatePercentageSplits(BigDecimal totalAmount, Map<String, Object> splitDetails) {
        if (splitDetails == null || !splitDetails.containsKey("percentages")) {
            throw new IllegalArgumentException("Percentage split requires 'percentages' in split details");
        }

        Map<String, Number> percentages = (Map<String, Number>) splitDetails.get("percentages");
        List<ExpenseSplit> splits = new ArrayList<>();
        BigDecimal totalPercentage = BigDecimal.ZERO;
        BigDecimal totalAssigned = BigDecimal.ZERO;
        
        // Calculate splits and track totals
        for (Map.Entry<String, Number> entry : percentages.entrySet()) {
            String userId = entry.getKey();
            BigDecimal percentage = new BigDecimal(entry.getValue().toString());
            totalPercentage = totalPercentage.add(percentage);
            
            BigDecimal amount = totalAmount.multiply(percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            amount = amount.setScale(2, RoundingMode.HALF_UP);
            totalAssigned = totalAssigned.add(amount);
            
            splits.add(new ExpenseSplit(null, userId, amount, percentage));
        }
        
        // Validate percentages total 100%
        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new IllegalArgumentException(
                String.format("Percentages must total 100%%, got %s%%", totalPercentage));
        }
        
        // Handle rounding differences by adjusting the first split
        BigDecimal difference = totalAmount.subtract(totalAssigned);
        if (difference.compareTo(BigDecimal.ZERO) != 0 && !splits.isEmpty()) {
            ExpenseSplit firstSplit = splits.get(0);
            firstSplit.setAmount(firstSplit.getAmount().add(difference));
        }
        
        logger.debug("Calculated {} percentage splits totaling {}", splits.size(), totalAmount);
        return splits;
    }

    /**
     * Calculate share-based splits.
     */
    @SuppressWarnings("unchecked")
    private List<ExpenseSplit> calculateShareSplits(BigDecimal totalAmount, Map<String, Object> splitDetails) {
        if (splitDetails == null || !splitDetails.containsKey("shares")) {
            throw new IllegalArgumentException("Share split requires 'shares' in split details");
        }

        Map<String, Number> shares = (Map<String, Number>) splitDetails.get("shares");
        List<ExpenseSplit> splits = new ArrayList<>();
        
        // Calculate total shares
        int totalShares = shares.values().stream()
                .mapToInt(Number::intValue)
                .sum();
        
        if (totalShares <= 0) {
            throw new IllegalArgumentException("Total shares must be greater than zero");
        }
        
        BigDecimal totalAssigned = BigDecimal.ZERO;
        List<ExpenseSplit> tempSplits = new ArrayList<>();
        
        // Calculate splits based on shares
        for (Map.Entry<String, Number> entry : shares.entrySet()) {
            String userId = entry.getKey();
            Integer userShares = entry.getValue().intValue();
            
            BigDecimal shareRatio = BigDecimal.valueOf(userShares).divide(BigDecimal.valueOf(totalShares), 4, RoundingMode.HALF_UP);
            BigDecimal amount = totalAmount.multiply(shareRatio).setScale(2, RoundingMode.HALF_UP);
            totalAssigned = totalAssigned.add(amount);
            
            ExpenseSplit split = new ExpenseSplit(null, userId, amount, userShares);
            tempSplits.add(split);
        }
        
        // Handle rounding differences by adjusting the first split
        BigDecimal difference = totalAmount.subtract(totalAssigned);
        if (difference.compareTo(BigDecimal.ZERO) != 0 && !tempSplits.isEmpty()) {
            ExpenseSplit firstSplit = tempSplits.get(0);
            firstSplit.setAmount(firstSplit.getAmount().add(difference));
        }
        
        splits.addAll(tempSplits);
        logger.debug("Calculated {} share splits with {} total shares", splits.size(), totalShares);
        return splits;
    }
}