package com.cihbank.backend.routingSuggestion;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.team.Team;
import com.cihbank.backend.team.TeamRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.userteam.UserTeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RoutingSuggestionService {
    private final ReclamationRepository reclamationRepository;
    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;
    private final RoutingSuggestionRepository routingSuggestionRepository;
    private final AuditLogService auditLogService;
    public RoutingSuggestionService(ReclamationRepository reclamationRepository,AuditLogService auditLogService, TeamRepository teamRepository, UserTeamRepository userTeamRepository, RoutingSuggestionRepository routingSuggestionRepository){
        this.reclamationRepository = reclamationRepository;
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
        this.routingSuggestionRepository = routingSuggestionRepository;
        this.auditLogService = auditLogService;
    }
    public RoutingSuggestion generateSuggestion(Integer idReclamation){
        Reclamation rec = reclamationRepository.findById(idReclamation).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Réclamation introuvable !"));
        RestTemplate restTemplate = new RestTemplate();
        Map<String,String> body = Map.of("description" , rec.getDescription() );
        Map response = restTemplate.postForObject(
                "http://localhost:8001/routing",
                body,
                Map.class
        );
        String teamName = (String) response.get("team");
        Double score = ((Number) response.get("score")).doubleValue();
        Team team = teamRepository.findByName(teamName).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"équipe introuvable !"));
        User suggestedUser = userTeamRepository.findFirstByTeam_IdTeam(team.getIdTeam()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Relation utilisateur et équipe introuvable !")).getUser();
        List<String> keywords = (List<String>) response.get("keywords");
        RoutingSuggestion suggestion = new RoutingSuggestion();

        suggestion.setReclamation(rec);
        suggestion.setSuggestedTeam(team);
        suggestion.setSuggestedUser(suggestedUser);
        suggestion.setKeywords(String.join(",", keywords));
        suggestion.setScore(score.floatValue());
        suggestion.setRoutingContext(RoutingContext.INITIAL_ROUTING);
        suggestion.setRoutingStatus(RoutingStatus.PENDING);
        suggestion.setAccepted(false);
        suggestion.setDecidedAt(LocalDateTime.now());

        RoutingSuggestion saved =  routingSuggestionRepository.save(suggestion);
        auditLogService.log(AuditAction.AI_ROUTING_SUGGESTED,"IA_suggestion", saved.getIdRouting(), idReclamation,null);
        return saved;
    }
    public RoutingSuggestion getSuggestion(Integer idReclamation){
        return routingSuggestionRepository.findByReclamationIdReclamationAndRoutingStatus(idReclamation,RoutingStatus.PENDING).orElse(null);
    }
}
