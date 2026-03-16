package com.cihbank.backend.resolution;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="resolution")
public class Resolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idResolution;
    @ManyToOne
    @JoinColumn(name="id_reclamation")
    private Reclamation reclamation;
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createdAt;
    public Resolution(){}

    public Integer getIdResolution() {
        return idResolution;
    }

    public void setIdResolution(Integer idResolution) {
        this.idResolution = idResolution;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
