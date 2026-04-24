package com.cihbank.backend.plafonddecision.strategy;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.card.Card;
import com.cihbank.backend.card.CardRepository;
import com.cihbank.backend.plafonddecision.DecisionOutcome;
import com.cihbank.backend.plafonddecision.PlafondDecision;
import com.cihbank.backend.plafondproposal.PlafondProposal;
import com.cihbank.backend.plafondrequest.PlafondRequest;
import com.cihbank.backend.plafondrequest.PlafondRequestStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("VALIDE")
public class ValidStrategy implements PlafondDecisionStrategy {

    private final CardRepository cardRepository;
    private final AuditLogService auditLogService;

    public ValidStrategy(CardRepository cardRepository,
                          AuditLogService auditLogService) {
        this.cardRepository = cardRepository;
        this.auditLogService = auditLogService;
    }


    public void execute(PlafondProposal proposal,
                        PlafondRequest request,
                        PlafondDecision decision,
                        Integer proposalId,
                        Integer userId) {

        request.setStatus(PlafondRequestStatus.VALIDEE);
        request.setClosedAt(LocalDateTime.now());

        decision.setOutcome(DecisionOutcome.VALIDEE);

        Card card = request.getCard();
        card.setCurrentLimit(proposal.getProposedLimit());
        cardRepository.save(card);

        auditLogService.log(
                AuditAction.VALIDATE_PLAFOND_CHANGE,
                "Décision_plafond",
                proposalId,
                userId,
                null
        );
    }
}