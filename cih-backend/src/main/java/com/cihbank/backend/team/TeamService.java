package com.cihbank.backend.team;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.security.CustomUserDetails;
import com.cihbank.backend.user.UserRepository;
import com.cihbank.backend.userteam.UserTeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final AuditLogService auditLogService;

    public TeamService(TeamRepository teamRepository,AuditLogService auditLogService){
        this.teamRepository = teamRepository;
        this.auditLogService = auditLogService;
    }
    public Team createTeam(Team team){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        if (team.getName() == null || team.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom de l'équipe est requis");
        }
        Team saved = teamRepository.save(team);
        auditLogService.log(AuditAction.CREATE_TEAM,"Équipe",saved.getIdTeam(),currentUserId,null);
        return saved;
    }
    public Team updateTeam(Integer idTeam, Team updatedTeam){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        Team foundTeam = teamRepository.findById(idTeam).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
        foundTeam.setName(updatedTeam.getName());
        foundTeam.setDescription(updatedTeam.getDescription());
        foundTeam.setIsActive(updatedTeam.getIsActive());
        Team saved =  teamRepository.save(foundTeam);
        auditLogService.log(AuditAction.UPDATE_TEAM,"Équipe",saved.getIdTeam(),currentUserId,null);
        return saved;
    }
    public Team activateTeam(Integer idTeam){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        Team foundTeam = teamRepository.findById(idTeam).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
        foundTeam.setIsActive(true);
        Team saved = teamRepository.save(foundTeam);
        auditLogService.log(AuditAction.ACTIVATE_TEAM,"Équipe",saved.getIdTeam(),currentUserId,null);
        return saved;
    }
    public Team deactivateTeam(Integer idTeam){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        Team foundTeam = teamRepository.findById(idTeam).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
        foundTeam.setIsActive(false);
        Team saved = teamRepository.save(foundTeam);
        auditLogService.log(AuditAction.DEACTIVATE_TEAM,"Équipe",saved.getIdTeam(),currentUserId,null);
        return saved;
    }
    public Team getTeam(Integer idTeam){
        return teamRepository.findById(idTeam).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
    }
    public List<Team> getAllTeam(){
        return teamRepository.findAll();
    }
}
