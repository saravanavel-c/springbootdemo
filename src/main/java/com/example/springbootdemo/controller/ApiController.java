package com.example.springbootdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiController {

    @GetMapping("/health")
    public String health() {
        return "Application is Running";
    }

    @GetMapping("/api/info")
    public Map<String, String> info() {
        return Map.of(
                "Application", "Spring Boot Demo",
                "Database", "PostgreSQL",
                "Status", "Running"
        );
    }
}