package com.cihbank.backend.decision;

public class NotificationEvent {

    private Integer idReclamation;
    private String message;

    public NotificationEvent(Integer idReclamation, String message) {
        this.idReclamation = idReclamation;
        this.message = message;
    }

    public Integer getIdReclamation() { return idReclamation; }
    public String getMessage() { return message; }
}