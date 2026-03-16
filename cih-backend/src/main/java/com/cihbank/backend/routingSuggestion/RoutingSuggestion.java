package com.cihbank.backend.routingSuggestion;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.team.Team;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="routing_suggestion")
public class RoutingSuggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRouting;
    @ManyToOne
    @JoinColumn(name="id_reclamation", nullable = false, unique = true)
    private Reclamation reclamation;
    private Float score;
    @Column(columnDefinition = "TEXT")
    private String keywords;
    @Enumerated(EnumType.STRING)
    private RoutingContext routingContext;
    @Enumerated(EnumType.STRING)
    private RoutingStatus routingStatus;
    private Boolean accepted;
    private LocalDateTime decidedAt;
    @ManyToOne
    @JoinColumn(name="accepted_by")
    private User decidedBy;
    @ManyToOne
    @JoinColumn(name="suggested_team")
    private Team suggestedTeam;
    @ManyToOne
    @JoinColumn(name="suggested_user")
    private User suggestedUser;
    public RoutingSuggestion(){}

    public Integer getIdRouting() {
        return idRouting;
    }

    public void setIdRouting(Integer idRouting) {
        this.idRouting = idRouting;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public RoutingContext getRoutingContext() {
        return routingContext;
    }

    public void setRoutingContext(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }

    public User getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(User decidedBy) {
        this.decidedBy = decidedBy;
    }

    public Team getSuggestedTeam() {
        return suggestedTeam;
    }

    public void setSuggestedTeam(Team suggestedTeam) {
        this.suggestedTeam = suggestedTeam;
    }

    public User getSuggestedUser() {
        return suggestedUser;
    }

    public void setSuggestedUser(User suggestedUser) {
        this.suggestedUser = suggestedUser;
    }

    public RoutingStatus getRoutingStatus() {
        return routingStatus;
    }

    public void setRoutingStatus(RoutingStatus routingStatus) {
        this.routingStatus = routingStatus;
    }
}
