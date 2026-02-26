package com.cihbank.backend.reclamation;

import com.cihbank.backend.reclamation.enums.CanalType;
import com.cihbank.backend.reclamation.enums.PriorityLevel;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.reclamation.enums.ReclamationType;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
public class Reclamation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReclamation;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    private String reference;
    private String title;

    @Enumerated(EnumType.STRING)
    private ReclamationType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private CanalType canal;

    @Enumerated(EnumType.STRING)
    private ReclamationStatus status;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    public Integer getIdReclamation() {
        return idReclamation;
    }

    public void setIdReclamation(Integer idReclamation) {
        this.idReclamation = idReclamation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ReclamationStatus getStatus() {
        return status;
    }

    public void setStatus(ReclamationStatus status) {
        this.status = status;
    }

    public CanalType getCanal() {
        return canal;
    }

    public void setCanal(CanalType canal) {
        this.canal = canal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReclamationType getType() {
        return type;
    }

    public void setType(ReclamationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}