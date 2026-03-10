package com.cihbank.backend.userteam;

import com.cihbank.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTeamRepository extends JpaRepository<UserTeam,UserTeamId> {
    List<UserTeam> findByTeam_IdTeam(Integer teamId);
    List<UserTeam> findByUser_IdUser(Integer userId);
    boolean existsByUser_IdUserAndTeam_IdTeam(Integer userId, Integer teamId);
    Optional<UserTeam> findFirstByTeam_IdTeam(Integer idTeam);
}
