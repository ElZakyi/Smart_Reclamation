package com.cihbank.backend.otp;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class OTPVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOtp;

    private String codeHash;
    private LocalDateTime expiresAt;
    private Integer attemptCount;
    private Integer maxAttempts;
    @Enumerated(EnumType.STRING)
    private OTPStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime validatedAt;

    // 🔥 TEMP DATA (IMPORTANT)
    private Integer tempUserId;
    private Integer tempCardId;
    private Double tempLimit;
    private String tempJustification;

    public Integer getIdOtp() {
        return idOtp;
    }

    public void setIdOtp(Integer idOtp) {
        this.idOtp = idOtp;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public void setCodeHash(String codeHash) {
        this.codeHash = codeHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public OTPStatus getStatus() {
        return status;
    }

    public void setStatus(OTPStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    public Integer getTempUserId() {
        return tempUserId;
    }

    public void setTempUserId(Integer tempUserId) {
        this.tempUserId = tempUserId;
    }

    public Integer getTempCardId() {
        return tempCardId;
    }

    public void setTempCardId(Integer tempCardId) {
        this.tempCardId = tempCardId;
    }

    public Double getTempLimit() {
        return tempLimit;
    }

    public void setTempLimit(Double tempLimit) {
        this.tempLimit = tempLimit;
    }

    public String getTempJustification() {
        return tempJustification;
    }

    public void setTempJustification(String tempJustification) {
        this.tempJustification = tempJustification;
    }
}
