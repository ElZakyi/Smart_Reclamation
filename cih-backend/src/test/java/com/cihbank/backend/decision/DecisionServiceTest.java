package com.cihbank.backend.decision;

import com.cihbank.backend.ai.ClassificationResultRepository;
import com.cihbank.backend.decisionproposal.DecisionProposal;
import com.cihbank.backend.decisionproposal.DecisionProposalRepository;
import com.cihbank.backend.notification.NotificationService;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import com.cihbank.backend.workflowtransition.WorkflowTransition;
import com.cihbank.backend.workflowtransition.WorkflowTransitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DecisionServiceTest {

    @Autowired
    private DecisionService decisionService;

    @Autowired
    private ReclamationRepository reclamationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DecisionProposalRepository decisionProposalRepository;

    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;

    @MockitoBean
    private NotificationService notificationService; // éviter email réel
    @Autowired
    private ClassificationResultRepository classificationResultRepository;
    @Autowired
    private DecisionRepository decisionRepository;

    private Reclamation reclamation;
    private User responsable;
    private DecisionProposal proposal;

    @BeforeEach
    void setup(){
        decisionRepository.deleteAll(); // 🔥 AJOUTE CA
        classificationResultRepository.deleteAll();
        workflowTransitionRepository.deleteAll();
        decisionProposalRepository.deleteAll();
        reclamationRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // ROLE
        Role role = new Role();
        role.setName("RESPONSABLE");
        roleRepository.save(role);

        // USER
        responsable = new User();
        responsable.setFullName("Resp");
        responsable.setEmail("resp@test.com");
        responsable.setPassword("1234");
        responsable.setPasswordHash("dummy");
        userRepository.save(responsable);

        // RECLAMATION
        reclamation = new Reclamation();
        reclamation.setReference("REC-1");
        reclamation.setStatus(ReclamationStatus.EN_VALIDATION);
        reclamationRepository.save(reclamation);

        // PROPOSAL
        proposal = new DecisionProposal();
        proposal.setReclamation(reclamation);
        proposal.setActive(true);
        proposal.setCreatedAt(LocalDateTime.now());
        decisionProposalRepository.save(proposal);

        // WORKFLOW
        WorkflowTransition t = new WorkflowTransition();
        t.setFromStatus(ReclamationStatus.EN_VALIDATION);
        t.setToStatus(ReclamationStatus.CLOTUREE);
        t.setRole(role);
        t.setActive(true);
        t.setCreatedAt(LocalDateTime.now());
        workflowTransitionRepository.save(t);
    }

    // =============================
    // ✅ TEST ACCEPT
    // =============================
    @Test
    void shouldAcceptDecision(){

        decisionService.acceptDecision(
                reclamation.getIdReclamation(),
                proposal.getIdDecisionProposal(),
                responsable.getIdUser(),
                "OK"
        );

        Reclamation updated = reclamationRepository.findById(reclamation.getIdReclamation()).get();

        assertEquals(ReclamationStatus.CLOTUREE, updated.getStatus());

        DecisionProposal updatedProposal = decisionProposalRepository.findById(proposal.getIdDecisionProposal()).get();
        assertFalse(updatedProposal.getActive());
    }

    // =============================
    // ❌ TEST TRANSITION INTERDITE
    // =============================
    @Test
    void shouldFailIfTransitionNotAllowed(){

        workflowTransitionRepository.deleteAll(); // aucune transition

        assertThrows(ResponseStatusException.class, () -> {

            decisionService.acceptDecision(
                    reclamation.getIdReclamation(),
                    proposal.getIdDecisionProposal(),
                    responsable.getIdUser(),
                    "FAIL"
            );

        });
    }

    // =============================
    // ✅ TEST REJECT
    // =============================
    @Test
    void shouldRejectDecision(){

        // ajouter transition retour
        WorkflowTransition t = new WorkflowTransition();
        t.setFromStatus(ReclamationStatus.EN_VALIDATION);
        t.setToStatus(ReclamationStatus.AFFECTEE);
        t.setRole(roleRepository.findAll().get(0));
        t.setActive(true);
        t.setCreatedAt(LocalDateTime.now());
        workflowTransitionRepository.save(t);

        decisionService.rejectDecision(
                reclamation.getIdReclamation(),
                proposal.getIdDecisionProposal(),
                responsable.getIdUser(),
                "REFUS"
        );

        Reclamation updated = reclamationRepository.findById(reclamation.getIdReclamation()).get();

        assertEquals(ReclamationStatus.AFFECTEE, updated.getStatus());
    }

}