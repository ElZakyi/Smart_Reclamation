package com.cihbank.backend.reclamationassignment;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.routingSuggestion.RoutingSuggestion;
import com.cihbank.backend.routingSuggestion.RoutingSuggestionRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReclamationAssignmentService {
    private final RoutingSuggestionRepository routingSuggestionRepository;
    private final ReclamationAssignmentRepository reclamationAssignmentRepository;
    private final UserRepository userRepository;
    private final ReclamationRepository reclamationRepository;
    public ReclamationAssignmentService(RoutingSuggestionRepository routingSuggestionRepository, ReclamationAssignmentRepository reclamationAssignmentRepository, UserRepository userRepository, ReclamationRepository reclamationRepository){
        this.routingSuggestionRepository = routingSuggestionRepository;
        this.reclamationAssignmentRepository = reclamationAssignmentRepository;
        this.userRepository = userRepository;
        this.reclamationRepository = reclamationRepository;
    }
    public ReclamationAssignment acceptSuggestion(Integer idSuggestion, Integer idAgent){
        RoutingSuggestion routingSuggestion = routingSuggestionRepository.findById(idSuggestion).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Suggéstion IA introuvable !"));
        Reclamation rec = routingSuggestion.getReclamation();
        Optional<ReclamationAssignment> current = reclamationAssignmentRepository.findByReclamationIdReclamationAndIsCurrentTrue(rec.getIdReclamation());
        current.ifPresent(a->{
            a.setCurrent(false);
            reclamationAssignmentRepository.save(a);
        });
        ReclamationAssignment reclamationAssignment = new ReclamationAssignment();
        reclamationAssignment.setReclamation(rec);
        reclamationAssignment.setAssignedAt(LocalDateTime.now());
        reclamationAssignment.setCurrent(true);
        reclamationAssignment.setUser(routingSuggestion.getSuggestedUser());
        reclamationAssignment.setTeam(routingSuggestion.getSuggestedTeam());
        reclamationAssignment.setReason("AI_ROUTING_ACCEPTED");
        reclamationAssignmentRepository.save(reclamationAssignment);
        routingSuggestion.setAccepted(true);
        User agent = userRepository.findById(idAgent).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        routingSuggestion.setAcceptedBy(agent);
        routingSuggestion.setDecidedAt(LocalDateTime.now());
        routingSuggestionRepository.save(routingSuggestion);

        rec.setStatus(ReclamationStatus.AFFECTEE);
        reclamationRepository.save(rec);

        return reclamationAssignment;
    }
    public void rejectSuggestion(Integer idSuggestion, Integer idAgent){
        RoutingSuggestion routingSuggestion = routingSuggestionRepository.findById(idSuggestion).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Suggéstion IA introuvable !"));
        User agent = userRepository.findById(idAgent).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        routingSuggestion.setAccepted(false);
        routingSuggestion.setAcceptedBy(agent);
        routingSuggestion.setDecidedAt(LocalDateTime.now());
        routingSuggestionRepository.save(routingSuggestion);
    }
}
