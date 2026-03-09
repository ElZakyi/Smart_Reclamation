package com.cihbank.backend.ai;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.enums.CanalType;
import com.cihbank.backend.reclamation.enums.ReclamationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="classification_result")
public class ClassificationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idClassification;
    @OneToOne
    @JoinColumn(name="id_reclamation", nullable = false, unique = true)
    private Reclamation reclamation;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReclamationType predictedType;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalType predictedCanal;
    @Column(nullable = false)
    private String suggestedTitle;
    @Column(nullable = false)
    private Double confidenceScore;
    @Column(nullable = false)
    private String modelVersion;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    public ClassificationResult(){}

    public Integer getIdClassification() {
        return idClassification;
    }

    public void setIdClassification(Integer idClassification) {
        this.idClassification = idClassification;
    }

    public Reclamation getReclamation() {
        return reclamation;
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
    }

    public ReclamationType getPredictedType() {
        return predictedType;
    }

    public void setPredictedType(ReclamationType predictedType) {
        this.predictedType = predictedType;
    }

    public CanalType getPredictedCanal() {
        return predictedCanal;
    }

    public void setPredictedCanal(CanalType predictedCanal) {
        this.predictedCanal = predictedCanal;
    }

    public String getSuggestedTitle() {
        return suggestedTitle;
    }

    public void setSuggestedTitle(String suggestedTitle) {
        this.suggestedTitle = suggestedTitle;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
