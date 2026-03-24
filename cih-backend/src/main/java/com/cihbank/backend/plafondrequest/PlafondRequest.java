package com.cihbank.backend.plafondrequest;

import com.cihbank.backend.card.Card;
import com.cihbank.backend.team.Team;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PlafondRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlafondRequest;

    @ManyToOne
    @JoinColumn(name = "id_card")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
    @ManyToOne
    @JoinColumn(name="id_team")
    private Team team;
    private Double requestedLimit;

    @Column(length = 1000)
    private String justification;

    @Enumerated(EnumType.STRING)
    private PlafondRequestStatus status;

    private LocalDateTime createdAt;

    // ===== GETTERS / SETTERS =====

    public Integer getIdPlafondRequest() { return idPlafondRequest; }
    public void setIdPlafondRequest(Integer id) { this.idPlafondRequest = id; }

    public Card getCard() { return card; }
    public void setCard(Card card) { this.card = card; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getRequestedLimit() { return requestedLimit; }
    public void setRequestedLimit(Double requestedLimit) { this.requestedLimit = requestedLimit; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public PlafondRequestStatus getStatus() { return status; }
    public void setStatus(PlafondRequestStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}