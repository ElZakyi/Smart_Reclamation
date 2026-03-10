package com.cihbank.backend.userteam;

import com.cihbank.backend.team.Team;
import com.cihbank.backend.team.TeamRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserTeamService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;
    public UserTeamService(UserRepository userRepository, TeamRepository teamRepository, UserTeamRepository userTeamRepository){
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.userTeamRepository = userTeamRepository;
    }
    @Transactional
    public void assignUserToTeam(Integer teamId, Integer userId){
        User userFound = userRepository.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        Team teamFound = teamRepository.findById(teamId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Équipe introuvable !"));
        if(!teamFound.getIsActive()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'équipe est inactive ! ");
        if(!userFound.getIsActive()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"L'utilisateur est inactif !");
        boolean isClient = userFound.getUserRoles().stream().anyMatch(ur->ur.getRole().getName().equals("CLIENT"));
        if(isClient) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Un client ne peut pas être affecté à une équipe !");
        if (userTeamRepository.existsByUser_IdUserAndTeam_IdTeam(userId, teamId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Déjà membre !");
        UserTeam userTeam = new UserTeam();

        UserTeamId id = new UserTeamId(userId, teamId);

        userTeam.setId(id);
        userTeam.setUser(userFound);
        userTeam.setTeam(teamFound);
        userTeam.setJoinedAt(LocalDateTime.now());
        userTeamRepository.save(userTeam);
    }
    @Transactional
    public void removeUserFromTeam(Integer teamId, Integer userId){
        UserTeamId userTeamId = new UserTeamId(teamId,userId);
        if(!userTeamRepository.existsById(userTeamId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Adhésion introuvable !");
        userTeamRepository.deleteById(userTeamId);
    }
    public List<UserTeam> getMembersOfTeam(Integer teamId) {
        return userTeamRepository.findByTeam_IdTeam(teamId);
    }

    // ================= LIST TEAMS OF USER =================

    public List<UserTeam> getTeamsOfUser(Integer userId) {
        return userTeamRepository.findByUser_IdUser(userId);
    }
}
