package com.cihbank.backend.userteam;

import com.cihbank.backend.team.Team;
import com.cihbank.backend.user.User;
import com.cihbank.backend.userrole.UserRoleId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class UserTeamId implements Serializable {
    @Column(name = "id_user")
    private Integer userId;

    @Column(name = "id_team")
    private Integer teamId;
    public UserTeamId(){}
    public UserTeamId(Integer teamId, Integer userId){
        this.teamId = teamId;
        this.userId = userId;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTeamId)) return false;
        UserTeamId that = (UserTeamId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(teamId, that.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, teamId);
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}
