package com.example.springbootdemo.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private String userIdentifier;
    private String action;
    private String entityType;
    private String entityId;
    private LocalDateTime timestamp;
    private String description;

    public AuditLogResponse() {
    }

    public AuditLogResponse(Long id, String userIdentifier, String action, String entityType, String entityId, LocalDateTime timestamp, String description) {
        this.id = id;
        this.userIdentifier = userIdentifier;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = timestamp;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
