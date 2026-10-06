package com.example.springbootdemo.controller;

import com.example.springbootdemo.dto.AuditLogResponse;
import com.example.springbootdemo.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs() {
        return ResponseEntity.ok(auditService.getAllAuditLogs());
    }
}
