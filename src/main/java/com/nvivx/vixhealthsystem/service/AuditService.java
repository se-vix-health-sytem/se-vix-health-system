package com.nvivx.vixhealthsystem.service;

import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.AuditLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {

    private final MockDatabase mockDatabase;

    public AuditService(MockDatabase mockDatabase) {
        this.mockDatabase = mockDatabase;
    }

    public void log(String action, String entityType, String entityId, String details) {
        String username = getCurrentUsername();
        AuditLog log = new AuditLog(action, entityType, entityId, username, details);
        mockDatabase.saveAuditLog(log);
        System.out.println("AUDIT: " + action + " - " + entityType + " " + entityId + " by " + username);
    }

    private String getCurrentUsername() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !authentication.getName().equals("anonymousUser")) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // Ignore - no security context yet
        }
        return "SYSTEM";
    }

    public List<AuditLog> getAllLogs() {
        return mockDatabase.getAuditLogs();
    }
}