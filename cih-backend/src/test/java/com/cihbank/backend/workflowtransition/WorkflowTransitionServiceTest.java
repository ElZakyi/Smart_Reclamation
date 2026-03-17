package com.cihbank.backend.workflowtransition;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkflowTransitionServiceTest {

    @Autowired
    private WorkflowTransitionService workflowTransitionService;

    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Reclamation reclamation;
    private Role role;

    @BeforeEach
    void setup(){

        // ROLE
        role = new Role();
        role.setName("AGENT");
        roleRepository.save(role);

        // RECLAMATION (juste objet, pas besoin DB ici)
        reclamation = new Reclamation();
        reclamation.setStatus(ReclamationStatus.RESOLUE);

        // WORKFLOW autorisé
        WorkflowTransition transition = new WorkflowTransition();
        transition.setFromStatus(ReclamationStatus.RESOLUE);
        transition.setToStatus(ReclamationStatus.EN_VALIDATION);
        transition.setRole(role);
        transition.setActive(true);
        transition.setCreatedAt(LocalDateTime.now());

        workflowTransitionRepository.save(transition);
    }

    // =========================
    // ✅ TRANSITION AUTORISÉE
    // =========================
    @Test
    void shouldAllowTransition(){

        assertDoesNotThrow(() -> {
            workflowTransitionService.validateTransition(
                    reclamation,
                    ReclamationStatus.EN_VALIDATION,
                    "AGENT"
            );
        });
    }

    // =========================
    // ❌ TRANSITION INTERDITE
    // =========================
    @Test
    void shouldFailIfTransitionNotAllowed(){

        assertThrows(ResponseStatusException.class, () -> {
            workflowTransitionService.validateTransition(
                    reclamation,
                    ReclamationStatus.CLOTUREE,
                    "AGENT"
            );
        });
    }

    // =========================
    // ❌ ROLE NON AUTORISÉ
    // =========================
    @Test
    void shouldFailIfWrongRole(){

        assertThrows(ResponseStatusException.class, () -> {
            workflowTransitionService.validateTransition(
                    reclamation,
                    ReclamationStatus.EN_VALIDATION,
                    "RESPONSABLE" // pas dans la DB
            );
        });
    }

    // =========================
    // ❌ TRANSITION INACTIVE
    // =========================
    @Test
    void shouldFailIfTransitionInactive(){

        workflowTransitionRepository.deleteAll();

        WorkflowTransition inactive = new WorkflowTransition();
        inactive.setFromStatus(ReclamationStatus.RESOLUE);
        inactive.setToStatus(ReclamationStatus.EN_VALIDATION);
        inactive.setRole(role);
        inactive.setActive(false); // ❌ inactive
        inactive.setCreatedAt(LocalDateTime.now());

        workflowTransitionRepository.save(inactive);

        assertThrows(ResponseStatusException.class, () -> {
            workflowTransitionService.validateTransition(
                    reclamation,
                    ReclamationStatus.EN_VALIDATION,
                    "AGENT"
            );
        });
    }
}