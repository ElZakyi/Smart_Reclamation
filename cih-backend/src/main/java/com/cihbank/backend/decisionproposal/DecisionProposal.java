package com.cihbank.backend.decisionproposal;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="decision_proposal")
public class DecisionProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDecisionProposal;
    @ManyToOne
    @JoinColumn(name="id_reclamation")
    private Reclamation reclamation;
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;
    @Enumerated(EnumType.STRING)
    private DecisionType type;
    @Column(columnDefinition = "TEXT")
    private String justification;
    private Boolean isActive;
    private LocalDateTime createdAt;
    public DecisionProposal(){}

    public Integer getIdDecisionProposal() {
        return idDecisionProposal;
    }

    public void setIdDecisionProposal(Integer idDecisionProposal) {
        this.idDecisionProposal = idDecisionProposal;
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

    public DecisionType getType() {
        return type;
    }

    public void setType(DecisionType type) {
        this.type = type;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
