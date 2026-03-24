package com.cihbank.backend.userteam;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/user-team")
public class UserTeamController {
    private final UserTeamService userTeamService;
    public UserTeamController(UserTeamService userTeamService){
        this.userTeamService = userTeamService;
    }
    @PreAuthorize("hasAuthority('ASSIGN_TEAM')")
    @PostMapping("/{idTeam}/user/{idUser}")
    public String assignUserToTeam(@PathVariable Integer idTeam, @PathVariable Integer idUser){
        userTeamService.assignUserToTeam(idTeam,idUser);
        return "Utilisateur affecté à l'équipe avec succès !";
    }
    @PreAuthorize("hasAuthority('REMOVE_TEAM_MEMBER')")
    @DeleteMapping("/{idTeam}/user/{idUser}")
    public String removeUserFromTeam(@PathVariable Integer idTeam, @PathVariable Integer idUser){
        userTeamService.removeUserFromTeam(idTeam,idUser);
        return "Membre retiré avec succès !";
    }
    @PreAuthorize("hasAuthority('VIEW_TEAM')")
    @GetMapping("/{idTeam}")
    public List<UserTeam> getMembersOfTeam(@PathVariable Integer idTeam){
        return userTeamService.getMembersOfTeam(idTeam);
    }
    @PreAuthorize("hasAuthority('VIEW_TEAM')")
    @GetMapping("/user/{userId}")
    public List<UserTeam> getTeamsOfUser(@PathVariable Integer userId){
        return userTeamService.getTeamsOfUser(userId);
    }
}
