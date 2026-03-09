package com.cihbank.backend.userteam;

import com.cihbank.backend.team.Team;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name="user_team")
public class UserTeam {
    @EmbeddedId
    private UserTeamId id;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name="id_user")
    private User user;
    @ManyToOne
    @MapsId("teamId")
    @JoinColumn(name="id_team")
    private Team team;
    private LocalDateTime joinedAt;
    public UserTeam(){}
    public UserTeam(User user, Team team, LocalDateTime joinedAt){
        this.user=user;
        this.team=team;
        this.joinedAt=joinedAt;
    }
    public UserTeamId getId() {
        return id;
    }

    public void setId(UserTeamId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
