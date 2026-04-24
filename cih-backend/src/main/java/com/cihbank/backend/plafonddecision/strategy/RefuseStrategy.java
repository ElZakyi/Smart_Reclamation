package com.cihbank.backend.plafonddecision.strategy;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.plafonddecision.DecisionOutcome;
import com.cihbank.backend.plafonddecision.PlafondDecision;
import com.cihbank.backend.plafondproposal.PlafondProposal;
import com.cihbank.backend.plafondrequest.PlafondRequest;
import com.cihbank.backend.plafondrequest.PlafondRequestStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("REFUSE")
public class RefuseStrategy implements PlafondDecisionStrategy {

    private final AuditLogService auditLogService;

    public RefuseStrategy(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void execute(PlafondProposal proposal,
                        PlafondRequest request,
                        PlafondDecision decision,
                        Integer proposalId,
                        Integer userId) {

        request.setStatus(PlafondRequestStatus.REFUSEE);
        request.setClosedAt(LocalDateTime.now());

        decision.setOutcome(DecisionOutcome.REFUSEE);

        auditLogService.log(
                AuditAction.REFUSE_PLAFOND_CHANGE,
                "Décision_plafond",
                proposalId,
                userId,
                null
        );
    }
}