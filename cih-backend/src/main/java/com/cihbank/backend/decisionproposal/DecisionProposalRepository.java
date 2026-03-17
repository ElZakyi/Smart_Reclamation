package com.cihbank.backend.decisionproposal;

import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DecisionProposalRepository extends JpaRepository<DecisionProposal,Integer> {
    List<DecisionProposal> findByReclamationIdReclamation(Integer idReclamation);
    List<DecisionProposal> findByIsActiveTrueAndReclamation_Status(ReclamationStatus status);
}
