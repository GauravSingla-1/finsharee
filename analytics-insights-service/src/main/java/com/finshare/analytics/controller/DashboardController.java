package com.finshare.analytics.controller;

import com.finshare.analytics.dto.DashboardDto;
import com.finshare.analytics.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/analytics")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard(
            @RequestHeader("X-Authenticated-User-ID") String userId,
            @RequestParam(defaultValue = "#{T(java.time.YearMonth).now().getYear()}") int year,
            @RequestParam(defaultValue = "#{T(java.time.YearMonth).now().getMonthValue()}") int month) {

        DashboardDto dashboard = dashboardService.getDashboardData(userId, year, month);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Analytics service is healthy");
    }
}