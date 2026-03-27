package com.cihbank.backend.plafonddecision;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.card.Card;
import com.cihbank.backend.card.CardRepository;
import com.cihbank.backend.plafondproposal.PlafondProposal;
import com.cihbank.backend.plafondproposal.PlafondProposalRepository;
import com.cihbank.backend.plafondrequest.*;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlafondDecisionService {

    private final PlafondDecisionRepository decisionRepository;
    private final PlafondRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final AuditLogService auditLogService;
    private final PlafondProposalRepository plafondProposalRepository;

    public PlafondDecisionService(
            PlafondDecisionRepository decisionRepository,
            PlafondRequestRepository requestRepository,
            UserRepository userRepository,
            CardRepository cardRepository,
            AuditLogService auditLogService,
            PlafondProposalRepository plafondProposalRepository
    ) {
        this.decisionRepository = decisionRepository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.auditLogService = auditLogService;
        this.plafondProposalRepository = plafondProposalRepository;
    }

    public void decide(Integer proposalId, Integer userId, String outcome, String motif) {

        PlafondProposal proposal = plafondProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal introuvable"));

        // 🔥 récupérer request depuis proposal
        PlafondRequest request = proposal.getPlafondRequest();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User introuvable"));

        PlafondDecision decision = new PlafondDecision();
        decision.setPlafondRequest(request);
        decision.setUser(user);
        decision.setMotif(motif);
        decision.setDecidedAt(LocalDateTime.now());

        // 🔥 LOGIQUE MÉTIER
        if (outcome.equals("VALIDE")) {

            request.setStatus(PlafondRequestStatus.VALIDEE);
            request.setClosedAt(LocalDateTime.now());
            decision.setOutcome(DecisionOutcome.VALIDEE);
            Card card = request.getCard();
            card.setCurrentLimit(proposal.getProposedLimit());
            cardRepository.save(card);

            auditLogService.log(AuditAction.VALIDATE_PLAFOND_CHANGE,
                    "Décision_plafond",
                    proposalId,
                    userId,
                    null);

        } else {

            request.setStatus(PlafondRequestStatus.REFUSEE);
            request.setClosedAt(LocalDateTime.now());
            decision.setOutcome(DecisionOutcome.REFUSEE);
            auditLogService.log(AuditAction.REFUSE_PLAFOND_CHANGE,
                    "Décision_plafond",
                    proposalId,
                    userId,
                    null);
        }
        decisionRepository.save(decision);
        requestRepository.save(request);
    }
}
