package com.cihbank.backend.team;

import com.cihbank.backend.user.UserRepository;
import com.cihbank.backend.userteam.UserTeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository){
        this.teamRepository = teamRepository;
    }
    public Team createTeam(Team team){
        if (team.getName() == null || team.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom de l'équipe est requis");
        }
        return teamRepository.save(team);
    }
    public Team updateTeam(Integer idTeam, Team updatedTeam){
        Team foundTeam = teamRepository.findById(idTeam).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
        foundTeam.setName(updatedTeam.getName());
        foundTeam.setDescription(updatedTeam.getDescription());
        foundTeam.setIsActive(updatedTeam.getIsActive());
        return teamRepository.save(foundTeam);
    }
    public Team activateTeam(Integer idTeam){
        Team foundTeam = teamRepository.findById(idTeam).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
        foundTeam.setIsActive(true);
        return teamRepository.save(foundTeam);
    }
    public Team deactivateTeam(Integer idTeam){
        Team foundTeam = teamRepository.findById(idTeam).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
        foundTeam.setIsActive(false);
        return teamRepository.save(foundTeam);
    }
    public Team getTeam(Integer idTeam){
        return teamRepository.findById(idTeam).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
    }
    public List<Team> getAllTeam(){
        return teamRepository.findAll();
    }
}
