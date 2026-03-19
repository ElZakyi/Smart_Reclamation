package com.cihbank.backend.decision;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLog;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.decisionproposal.DecisionProposal;
import com.cihbank.backend.decisionproposal.DecisionProposalRepository;
import com.cihbank.backend.notification.NotificationChannel;
import com.cihbank.backend.notification.NotificationService;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import com.cihbank.backend.workflowtransition.WorkflowTransition;
import com.cihbank.backend.workflowtransition.WorkflowTransitionService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class DecisionService {
    private final DecisionRepository decisionRepository;
    private final ReclamationRepository reclamationRepository;
    private final UserRepository userRepository;
    private final DecisionProposalRepository decisionProposalRepository;
    private final NotificationService notificationService;
    private final WorkflowTransitionService workflowTransitionService;
    private final AuditLogService auditLogService;
    public DecisionService(DecisionRepository decisionRepository, AuditLogService auditLogService, ReclamationRepository reclamationRepository, UserRepository userRepository, DecisionProposalRepository decisionProposalRepository, NotificationService notificationService, WorkflowTransitionService workflowTransitionService){
        this.decisionRepository=decisionRepository;
        this.reclamationRepository = reclamationRepository;
        this.userRepository = userRepository;
        this.decisionProposalRepository = decisionProposalRepository;
        this.notificationService = notificationService;
        this.workflowTransitionService = workflowTransitionService;
        this.auditLogService = auditLogService;
    }
    @Transactional
    public void acceptDecision(Integer idReclamation, Integer idDecisionProposal, Integer idUser, String motif){
        Reclamation reclamation = reclamationRepository.findById(idReclamation).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Réclamation introuvable !"));
        workflowTransitionService.validateTransition(reclamation,ReclamationStatus.CLOTUREE,"RESPONSABLE");
        reclamation.setStatus(ReclamationStatus.CLOTUREE);
        Reclamation reclamationSaved = reclamationRepository.save(reclamation);
        User responsable = userRepository.findById(idUser).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        DecisionProposal decisionProposal = decisionProposalRepository.findById(idDecisionProposal).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Proposition introuvable !"));
        decisionProposal.setActive(false);
        decisionProposalRepository.save(decisionProposal);
        Decision decision = new Decision();
        decision.setDecisionType(decisionProposal.getType());
        decision.setMotif(motif);
        decision.setUser(responsable);
        decision.setReclamation(reclamationSaved);
        decision.setDecidedAt(LocalDateTime.now());
        decision.setOutcome(DecisionOutcome.VALIDE);
        Decision saved = decisionRepository.save(decision);
        auditLogService.log(AuditAction.DECIDE_CLOTURE,"Decision",saved.getIdDecision(),idUser,null);
        User client = reclamation.getUser();
        // notifier client
        notificationService.notifyUser(
                client,
                "Réclamation traitée",
                "Votre réclamation " + reclamation.getReference() + " a été traitée avec succès.",
                NotificationChannel.EMAIL
        );
    }
    @Transactional
    public void rejectDecision(Integer idReclamation,Integer idDecisionProposal, Integer idUser,String motif){
        Reclamation reclamation = reclamationRepository.findById(idReclamation).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Réclamation introuvable !"));
        workflowTransitionService.validateTransition(reclamation,ReclamationStatus.AFFECTEE,"RESPONSABLE");
        reclamation.setStatus(ReclamationStatus.AFFECTEE);
        Reclamation reclamationSaved = reclamationRepository.save(reclamation);
        User responsable = userRepository.findById(idUser).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        DecisionProposal decisionProposal = decisionProposalRepository.findById(idDecisionProposal).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Proposition introuvable !"));
        decisionProposal.setActive(false);
        decisionProposalRepository.save(decisionProposal);
        Decision decision = new Decision();
        decision.setDecisionType(decisionProposal.getType());
        decision.setMotif(motif);
        decision.setUser(responsable);
        decision.setReclamation(reclamationSaved);
        decision.setDecidedAt(LocalDateTime.now());
        decision.setOutcome(DecisionOutcome.REFUSE);
        Decision saved = decisionRepository.save(decision);
        auditLogService.log(AuditAction.DECIDE_REJET,"Décision",saved.getIdDecision(),idUser,null);
    }
}
