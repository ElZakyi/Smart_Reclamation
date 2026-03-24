package com.cihbank.backend.card;

import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="card")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCard;
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;
    private String cardNumberMasked;
    private String cardType;
    private Double currentLimit;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
    private LocalDateTime createdAt;
    public Card(){}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getIdCard() {
        return idCard;
    }

    public void setIdCard(Integer idCard) {
        this.idCard = idCard;
    }

    public String getCardNumberMasked() {
        return cardNumberMasked;
    }

    public void setCardNumberMasked(String cardNumberMasked) {
        this.cardNumberMasked = cardNumberMasked;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Double getCurrentLimit() {
        return currentLimit;
    }

    public void setCurrentLimit(Double currentLimit) {
        this.currentLimit = currentLimit;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
