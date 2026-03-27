package com.cihbank.backend.plafonddecision;
import com.cihbank.backend.plafondrequest.PlafondRequest;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="plafond_decision")
public class PlafondDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlafondDecision;

    @ManyToOne
    @JoinColumn(name = "id_plafond_request")
    private PlafondRequest plafondRequest;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
    @Enumerated(EnumType.STRING)
    private DecisionOutcome outcome; // VALIDE / REFUSE
    private String motif;
    private LocalDateTime decidedAt;

    public Integer getIdPlafondDecision() {
        return idPlafondDecision;
    }

    public void setIdPlafondDecision(Integer idPlafondDecision) {
        this.idPlafondDecision = idPlafondDecision;
    }

    public PlafondRequest getPlafondRequest() {
        return plafondRequest;
    }

    public void setPlafondRequest(PlafondRequest plafondRequest) {
        this.plafondRequest = plafondRequest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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