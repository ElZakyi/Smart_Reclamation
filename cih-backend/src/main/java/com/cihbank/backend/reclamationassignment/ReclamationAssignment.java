package com.cihbank.backend.reclamationassignment;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.team.Team;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="reclamation_assignment")
public class ReclamationAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAssignment;
    @ManyToOne
    @JoinColumn(name="id_reclamation")
    private Reclamation reclamation;
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;
    @ManyToOne
    @JoinColumn(name="id_team")
    private Team team;
    private LocalDateTime assignedAt;
    private String reason;
    private Boolean isCurrent;
    @ManyToOne
    @JoinColumn(name="assigned_by")
    private User assignedBy;
    public ReclamationAssignment(){}

    public Integer getIdAssignment() {
        return idAssignment;
    }

    public void setIdAssignment(Integer idAssignment) {
        this.idAssignment = idAssignment;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
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

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }
}
