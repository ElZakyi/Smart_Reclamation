package com.cihbank.backend.decision;

import com.cihbank.backend.decisionproposal.DecisionType;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="decision")
public class Decision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDecision;
    @ManyToOne
    @JoinColumn(name="id_reclamation")
    private Reclamation reclamation;
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;
    @Enumerated(EnumType.STRING)
    private DecisionType decisionType;
    @Enumerated(EnumType.STRING)
    private DecisionOutcome outcome;
    @Column(columnDefinition = "TEXT")
    private String motif;
    private LocalDateTime decidedAt;
    public Decision(){}

    public Integer getIdDecision() {
        return idDecision;
    }

    public void setIdDecision(Integer idDecision) {
        this.idDecision = idDecision;
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

    public DecisionType getDecisionType() {
        return decisionType;
    }

    public void setDecisionType(DecisionType decisionType) {
        this.decisionType = decisionType;
    }

    public DecisionOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(DecisionOutcome outcome) {
        this.outcome = outcome;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }
}
