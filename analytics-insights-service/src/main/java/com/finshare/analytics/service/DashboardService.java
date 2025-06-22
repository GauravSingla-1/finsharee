package com.finshare.analytics.service;

import com.finshare.analytics.dto.DashboardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public DashboardDto getDashboardData(String userId, int year, int month) {
        String key = String.format("%s:dashboard:%d-%02d", userId, year, month);
        
        Map<Object, Object> rawData = redisTemplate.opsForHash().entries(key);
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();
        BigDecimal totalSpend = BigDecimal.ZERO;
        
        for (Map.Entry<Object, Object> entry : rawData.entrySet()) {
            String category = (String) entry.getKey();
            BigDecimal amount = new BigDecimal((String) entry.getValue());
            spendingByCategory.put(category, amount);
            totalSpend = totalSpend.add(amount);
        }
        
        // If no data in Redis, return empty dashboard
        if (spendingByCategory.isEmpty()) {
            spendingByCategory.put("No expenses", BigDecimal.ZERO);
        }
        
        return new DashboardDto(spendingByCategory, totalSpend);
    }

    public void updateSpendingData(String userId, String category, BigDecimal amount, YearMonth yearMonth) {
        String key = String.format("%s:dashboard:%s", userId, yearMonth.toString());
        redisTemplate.opsForHash().increment(key, category, amount.doubleValue());
    }
}