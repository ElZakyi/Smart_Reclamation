package com.cihbank.backend.user;

import com.cihbank.backend.userrole.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idUser")
    private Integer idUser;

    @Column(nullable = false)
    private String fullname;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private String phone;
    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRoles;
    @Column(nullable = false)
    private Boolean isAccountLocked = false;
    @Column(nullable = false)
    private Boolean isAccountExpired = false;
    @Column(nullable = false)
    private Boolean isCredentialExpired = false;
    @Column
    private LocalDateTime passwordChangedAt;

    public User() {

    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullName(String fullname) {
        this.fullname = fullname;
    }
    public String getPassword(){return password;}
    public void setPassword(String password){
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }
    public Boolean getIsAccountLocked(){
        return isAccountLocked;
    }
    public void setIsAccountLocked(Boolean isAccountLocked){
        this.isAccountLocked = isAccountLocked;
    }
    public Boolean getIsAccountExpired(){
        return isAccountExpired;
    }
    public void setIsAccountExpired(Boolean isAccountExpired){
        this.isAccountExpired = isAccountExpired;
    }
    public Boolean getIsCredentialExpired(){
        return isCredentialExpired;
    }
    public void setIsCredentialExpired(Boolean isCredentialExpired){
        this.isCredentialExpired = isCredentialExpired;
    }
}
