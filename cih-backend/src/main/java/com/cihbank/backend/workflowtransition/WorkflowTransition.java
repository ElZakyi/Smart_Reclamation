package com.cihbank.backend.workflowtransition;

import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.role.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="workflow_transition")
public class WorkflowTransition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTransition;
    @Enumerated(EnumType.STRING)
    private ReclamationStatus fromStatus;
    @Enumerated(EnumType.STRING)
    private ReclamationStatus toStatus;
    @ManyToOne
    @JoinColumn(name="id_role")
    private Role role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    public WorkflowTransition(){}

    public Integer getIdTransition() {
        return idTransition;
    }

    public void setIdTransition(Integer idTransition) {
        this.idTransition = idTransition;
    }

    public ReclamationStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(ReclamationStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ReclamationStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(ReclamationStatus toStatus) {
        this.toStatus = toStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
