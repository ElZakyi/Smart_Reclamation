package com.cihbank.backend.audit;

public enum AuditAction {

    // ================= RECLAMATION =================
    CREATE_RECLAMATION,
    UPDATE_RECLAMATION,
    DELETE_RECLAMATION,

    // ================= AI =================
    AI_CLASSIFICATION,
    AI_ROUTING_SUGGESTED,

    // ================= ASSIGNMENT =================
    ASSIGN,
    REASSIGN,
    MANUAL_ASSIGNMENT,
    ACCEPT_ROUTING,
    REJECT_ROUTING,

    // ================= MESSAGE =================
    SEND_MESSAGE,

    // ================= ATTACHMENT =================
    UPLOAD_ATTACHMENT,
    DELETE_ATTACHMENT,

    // ================= RESOLUTION =================
    CREATE_RESOLUTION,

    // ================= DECISION =================
    PROPOSE_DECISION,
    DECIDE_REJET,
    DECIDE_CLOTURE,
    // ================= USER =================
    CREATE_USER,
    UPDATE_USER,
    ACTIVATE_USER,
    DEACTIVATE_USER,

    // ================= ROLE =================
    ASSIGN_ROLE,
    REMOVE_ROLE,
    UPDATE_ROLE,

    // ================= TEAM =================
    CREATE_TEAM,
    UPDATE_TEAM,
    ACTIVATE_TEAM,
    DEACTIVATE_TEAM,
    ASSIGN_USER_TO_TEAM,
    REMOVE_USER_FROM_TEAM,

    // ================= NOTIFICATION =================
    SEND_NOTIFICATION,

    // ================= PLAFOND =================
    CREATE_PLAFOND_REQUEST,
    PROPOSE_PLAFOND_CHANGE,
    VALIDATE_PLAFOND_CHANGE,
    REFUSE_PLAFOND_CHANGE
}
