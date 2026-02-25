package com.cihbank.backend.userrole;

import com.cihbank.backend.role.Role;
import com.cihbank.backend.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="user_role")
public class UserRole {
    @EmbeddedId
    private UserRoleId  id;
    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name="id_user")
    @JsonIgnore
    private User user;
    @ManyToOne
    @MapsId("idRole")
    @JoinColumn(name="id_role")
    private Role role;

    private LocalDateTime assignedAt;
    public UserRole(){}
    public UserRole(User user, Role role, LocalDateTime assignedAt){
        this.user = user;
        this.role = role;
        this.assignedAt = assignedAt;
        this.id = new UserRoleId(user.getIdUser(), role.getIdRole());
    }
    public UserRoleId getId(){
        return id;
    }
    public void setId(UserRoleId id){
        this.id = id;
    }
    public User getUser(){
        return user;
    }
    public void setUser(User user){
        this.user = user;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
