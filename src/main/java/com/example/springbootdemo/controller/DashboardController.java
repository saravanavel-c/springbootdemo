package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.DashboardResponse;
import com.example.springbootdemo.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = false) Long customerId) {

        return ResponseEntity.ok(dashboardService.getDashboardData(customerId));
    }
}
