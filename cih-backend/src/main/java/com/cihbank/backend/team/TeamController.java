package com.cihbank.backend.team;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    private final TeamService teamService;
    public TeamController(TeamService teamService){
        this.teamService = teamService;
    }
    @PreAuthorize("hasAuthority('CREATE_TEAM')")
    @PostMapping
    public Team createTeam(@RequestBody Team team){
        return teamService.createTeam(team);
    }
    @PreAuthorize("hasAuthority('UPDATE_TEAM')")
    @PutMapping("/{teamId}")
    public Team updateTeam(@PathVariable Integer teamId,@RequestBody Team updatedTeam){
        return teamService.updateTeam(teamId,updatedTeam);
    }
    @PreAuthorize("hasAuthority('ACTIVATE_TEAM')")
    @PatchMapping("{teamId}/activate")
    public String activateTeam(@PathVariable Integer teamId){
        teamService.activateTeam(teamId);
        return "Team activated successfully !";
    }
    @PreAuthorize("hasAuthority('DEACTIVATE_TEAM')")
    @PatchMapping("{teamId}/deactivate")
    public String deactivateTeam(@PathVariable Integer teamId){
        teamService.deactivateTeam(teamId);
        return "Team deactivated successfully !";
    }
    @PreAuthorize("hasAuthority('VIEW_TEAM')")
    @GetMapping("/{idTeam}")
    public Team getTeam(@PathVariable Integer idTeam){
        return teamService.getTeam(idTeam);
    }
    @PreAuthorize("hasAuthority('VIEW_TEAM')")
    @GetMapping
    public List<Team> getAllTeam(){
        return teamService.getAllTeam();
    }


}
