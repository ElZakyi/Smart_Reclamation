package com.cihbank.backend.workflowtransition;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.role.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WorkflowTransitionService {
    private final WorkflowTransitionRepository workflowTransitionRepository;
    public WorkflowTransitionService(WorkflowTransitionRepository workflowTransitionRepository){
        this.workflowTransitionRepository = workflowTransitionRepository;
    }
    public void validateTransition(Reclamation rec, ReclamationStatus newStatus, String role){
        boolean allowed = workflowTransitionRepository.findByFromStatusAndToStatusAndRole_NameAndIsActiveTrue(rec.getStatus(),newStatus,role).isPresent();
        if(!allowed){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Cette transition est invalide pour ce rôle !");
        }
    }
}
