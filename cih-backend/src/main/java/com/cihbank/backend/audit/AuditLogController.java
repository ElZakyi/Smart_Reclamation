package com.cihbank.backend.audit;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {
    private final AuditLogRepository auditLogRepository;
    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    public List<AuditLog> getAll(){
        return auditLogRepository.findAll();
    }
}