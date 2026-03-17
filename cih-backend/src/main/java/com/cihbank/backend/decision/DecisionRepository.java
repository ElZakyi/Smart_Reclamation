package com.cihbank.backend.decision;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DecisionRepository extends JpaRepository<Decision,Integer> {
    List<Decision> findByReclamationIdReclamation(Integer idReclamation);
}
