package com.cihbank.backend.attachment;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAttachment;
    @ManyToOne
    @JoinColumn(name="id_reclamation",nullable = false)
    private Reclamation reclamation;
    @ManyToOne
    @JoinColumn(name="id_user")
    private User user;
    private String fileName;
    private String fileType;
    private Double fileSize;
    private String storageUrl;
    private LocalDateTime uploadedAt;

    public Integer getIdAttachment() {
        return idAttachment;
    }

    public void setIdAttachment(Integer idAttachment) {
        this.idAttachment = idAttachment;
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

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Double getFileSize() { return fileSize; }
    public void setFileSize(Double fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }
}
