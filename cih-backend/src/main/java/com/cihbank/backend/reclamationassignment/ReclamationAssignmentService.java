package com.cihbank.backend.reclamationassignment;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.notification.NotificationService;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.routingSuggestion.RoutingStatus;
import com.cihbank.backend.routingSuggestion.RoutingSuggestion;
import com.cihbank.backend.routingSuggestion.RoutingSuggestionRepository;
import com.cihbank.backend.team.Team;
import com.cihbank.backend.team.TeamRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamationAssignmentService {
    private final RoutingSuggestionRepository routingSuggestionRepository;
    private final ReclamationAssignmentRepository reclamationAssignmentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ReclamationRepository reclamationRepository;
    private final TeamRepository teamRepository;
    private final AuditLogService auditLogService;
    public ReclamationAssignmentService(RoutingSuggestionRepository routingSuggestionRepository,AuditLogService auditLogService, ReclamationAssignmentRepository reclamationAssignmentRepository, UserRepository userRepository, ReclamationRepository reclamationRepository, NotificationService notificationService,TeamRepository teamRepository){
        this.routingSuggestionRepository = routingSuggestionRepository;
        this.reclamationAssignmentRepository = reclamationAssignmentRepository;
        this.userRepository = userRepository;
        this.reclamationRepository = reclamationRepository;
        this.notificationService = notificationService;
        this.teamRepository = teamRepository;
        this.auditLogService = auditLogService;
    }
    public ReclamationAssignment acceptSuggestion(Integer idSuggestion, Integer idResponsable){
        RoutingSuggestion routingSuggestion = routingSuggestionRepository.findById(idSuggestion).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Suggéstion IA introuvable !"));
        Reclamation rec = routingSuggestion.getReclamation();
        Optional<ReclamationAssignment> current = reclamationAssignmentRepository.findByReclamationIdReclamationAndIsCurrentTrue(rec.getIdReclamation());
        current.ifPresent(a->{
            a.setCurrent(false);
            reclamationAssignmentRepository.save(a);
        });
        User responsable = userRepository.findById(idResponsable).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur non trouvée !"));
        ReclamationAssignment reclamationAssignment = new ReclamationAssignment();
        reclamationAssignment.setReclamation(rec);
        reclamationAssignment.setAssignedAt(LocalDateTime.now());
        reclamationAssignment.setCurrent(true);
        reclamationAssignment.setUser(routingSuggestion.getSuggestedUser());
        reclamationAssignment.setTeam(routingSuggestion.getSuggestedTeam());
        reclamationAssignment.setAssignedBy(responsable);
        reclamationAssignment.setReason("AI_ROUTING_ACCEPTED");
        ReclamationAssignment saved = reclamationAssignmentRepository.save(reclamationAssignment);
        auditLogService.log(AuditAction.ACCEPT_ROUTING,"Assignment",saved.getIdAssignment(),idResponsable,null);
        routingSuggestion.setAccepted(true);
        routingSuggestion.setRoutingStatus(RoutingStatus.ACCEPTED);
        User agent = userRepository.findById(idResponsable).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        routingSuggestion.setDecidedBy(agent);
        routingSuggestion.setDecidedAt(LocalDateTime.now());
        routingSuggestionRepository.save(routingSuggestion);

        rec.setStatus(ReclamationStatus.AFFECTEE);
        reclamationRepository.save(rec);

        notificationService.notifyTeam(routingSuggestion.getSuggestedTeam(),rec);
        return saved;
    }
    public void rejectSuggestion(Integer idSuggestion, Integer idResponsable){
        RoutingSuggestion routingSuggestion = routingSuggestionRepository.findById(idSuggestion).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Suggéstion IA introuvable !"));
        User responsable = userRepository.findById(idResponsable).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        routingSuggestion.setAccepted(false);
        routingSuggestion.setRoutingStatus(RoutingStatus.REJECTED);
        routingSuggestion.setDecidedBy(responsable);
        routingSuggestion.setDecidedAt(LocalDateTime.now());
        routingSuggestionRepository.save(routingSuggestion);
        auditLogService.log(AuditAction.REJECT_ROUTING,"routing_suggestion",routingSuggestion.getIdRouting(),idResponsable,null);
    }
    public ReclamationAssignment manualAssignment(Integer idSuggestion, Integer idResponsable,Integer idTeam,Integer idAgent ){
        RoutingSuggestion routingSuggestion = routingSuggestionRepository.findById(idSuggestion).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Routing suggéstion introuvable !"));
        User responsable = userRepository.findById(idResponsable).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        Team team = teamRepository.findById(idTeam).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
        User agent = userRepository.findById(idAgent).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        Reclamation reclamation = routingSuggestion.getReclamation();
        reclamation.setStatus(ReclamationStatus.AFFECTEE);
        reclamationRepository.save(reclamation);
        ReclamationAssignment reclamationAssignment = new ReclamationAssignment();
        reclamationAssignment.setAssignedBy(responsable);
        reclamationAssignment.setTeam(team);
        reclamationAssignment.setUser(agent);
        reclamationAssignment.setAssignedAt(LocalDateTime.now());
        reclamationAssignment.setReclamation(reclamation);
        reclamationAssignment.setCurrent(true);
        reclamationAssignment.setReason("MANUAL_ROUTING");
        ReclamationAssignment saved =  reclamationAssignmentRepository.save(reclamationAssignment);
        auditLogService.log(AuditAction.MANUAL_ASSIGNMENT,"Réclamation_assignement",saved.getIdAssignment(),idResponsable,null);
        return saved;
    }
    public List<ReclamationAssignment> getAssignmentsForAgent(Integer idAgent){

        return reclamationAssignmentRepository
                .findByUser_IdUserAndIsCurrentTrueAndReclamation_StatusIn(
                        idAgent,
                        List.of(
                                ReclamationStatus.AFFECTEE,
                                ReclamationStatus.EN_ATTENTE_CLIENT,
                                ReclamationStatus.RESOLUE
                        )
                );
    }
}
