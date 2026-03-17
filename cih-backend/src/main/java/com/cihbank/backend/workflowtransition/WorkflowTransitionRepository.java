package com.cihbank.backend.workflowtransition;

import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition,Integer> {
    Optional<WorkflowTransition> findByFromStatusAndToStatusAndRole_NameAndIsActiveTrue(
            ReclamationStatus from,
            ReclamationStatus to,
            String roleName
    );
}
