package com.cihbank.backend.decisionproposal;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import com.cihbank.backend.workflowtransition.WorkflowTransitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // 🔥 rollback auto → pas besoin deleteAll
class DecisionProposalServiceTest {

    @Autowired
    private DecisionProposalService decisionProposalService;

    @Autowired
    private DecisionProposalRepository decisionProposalRepository;

    @Autowired
    private ReclamationRepository reclamationRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private WorkflowTransitionService workflowTransitionService;

    private Reclamation reclamation;
    private User agent;

    @BeforeEach
    void setup(){

        // USER
        agent = new User();
        agent.setFullName("Agent Test");
        agent.setEmail("agent@test.com");
        agent.setPassword("1234");
        agent.setPasswordHash("dummy");
        userRepository.save(agent);

        // RECLAMATION
        reclamation = new Reclamation();
        reclamation.setReference("REC-TEST");
        reclamation.setStatus(ReclamationStatus.RESOLUE); // important avant transition
        reclamationRepository.save(reclamation);

        // MOCK workflow
        doNothing().when(workflowTransitionService)
                .validateTransition(any(), any(), any());
    }

    // =========================
    // ✅ TEST CREATE PROPOSITION
    // =========================
    @Test
    void shouldCreateDecisionProposal(){

        DecisionProposal proposal = decisionProposalService.createProposition(
                reclamation.getIdReclamation(),
                DecisionType.CLOTURE,
                agent.getIdUser(),
                "OK"
        );

        assertNotNull(proposal.getIdDecisionProposal());
        assertEquals(true, proposal.getActive());
        assertEquals("OK", proposal.getJustification());
        assertEquals(agent.getIdUser(), proposal.getUser().getIdUser());

        // 🔥 vérifier changement de status
        Reclamation updated = reclamationRepository.findById(reclamation.getIdReclamation()).get();
        assertEquals(ReclamationStatus.EN_VALIDATION, updated.getStatus());
    }

    // =========================
    // ❌ TEST RECLAMATION NOT FOUND
    // =========================
    @Test
    void shouldFailIfReclamationNotFound(){

        assertThrows(Exception.class, () -> {
            decisionProposalService.createProposition(
                    999,
                    DecisionType.CLOTURE,
                    agent.getIdUser(),
                    "FAIL"
            );
        });
    }

    // =========================
    // ❌ TEST USER NOT FOUND
    // =========================
    @Test
    void shouldFailIfUserNotFound(){

        assertThrows(Exception.class, () -> {
            decisionProposalService.createProposition(
                    reclamation.getIdReclamation(),
                    DecisionType.CLOTURE,
                    999,
                    "FAIL"
            );
        });
    }

    // =========================
    // ✅ TEST FIND ACTIVE
    // =========================
    @Test
    void shouldReturnActiveProposals(){

        decisionProposalService.createProposition(
                reclamation.getIdReclamation(),
                DecisionType.CLOTURE,
                agent.getIdUser(),
                "OK"
        );

        var list = decisionProposalService.findByIsActiveTrueAndReclamationStatus();

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }
}