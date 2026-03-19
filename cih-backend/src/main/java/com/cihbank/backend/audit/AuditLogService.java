package com.cihbank.backend.audit;

import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository,
                        UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void log(
            AuditAction action,
            String entityType,
            Integer entityId,
            Integer userId,
            HttpServletRequest request
    ){
        AuditLog log = new AuditLog();

        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setCreatedAt(LocalDateTime.now());

        if(userId != null){
            User user = userRepository.findById(userId).orElse(null);
            log.setUser(user);
        }

        if(request != null){
            log.setIpAddress(request.getRemoteAddr());
            log.setUserAgent(request.getHeader("User-Agent"));
        }

        auditLogRepository.save(log);
    }
}