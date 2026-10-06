package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.AuditLogResponse;
import com.example.springbootdemo.entity.AuditLog;
import com.example.springbootdemo.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String action, String entityType, String entityId, String description) {
        String username = getCurrentUserIdentifier();
        AuditLog auditLog = new AuditLog(username, action, entityType, entityId, description);
        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc()
                .stream()
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUserIdentifier(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getTimestamp(),
                        log.getDescription()
                ))
                .collect(Collectors.toList());
    }

    private String getCurrentUserIdentifier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "system";
    }
}
