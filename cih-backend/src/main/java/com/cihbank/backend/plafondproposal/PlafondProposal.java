package com.cihbank.backend.plafondproposal;

import com.cihbank.backend.plafondrequest.PlafondRequest;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PlafondProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlafondProposal;

    @ManyToOne
    @JoinColumn(name = "id_plafond_request")
    private PlafondRequest plafondRequest;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    private Double proposedLimit;
    private String justification;
    private LocalDateTime createdAt;

    public Integer getIdPlafondProposal() {
        return idPlafondProposal;
    }

    public void setIdPlafondProposal(Integer idPlafondProposal) {
        this.idPlafondProposal = idPlafondProposal;
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

    public Double getProposedLimit() {
        return proposedLimit;
    }

    public void setProposedLimit(Double proposedLimit) {
        this.proposedLimit = proposedLimit;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
