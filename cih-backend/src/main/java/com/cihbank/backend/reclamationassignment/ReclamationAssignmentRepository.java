package com.cihbank.backend.reclamationassignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReclamationAssignmentRepository extends JpaRepository<ReclamationAssignment,Integer> {
    Optional<ReclamationAssignment> findByReclamationIdReclamationAndIsCurrentTrue(Integer id);
    List<ReclamationAssignment> findByReclamationIdReclamation(Integer id);
}
