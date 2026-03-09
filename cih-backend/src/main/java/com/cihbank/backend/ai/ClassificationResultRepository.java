package com.cihbank.backend.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassificationResultRepository extends JpaRepository<ClassificationResult,Integer> {
    Optional<ClassificationResult> findByReclamation_IdReclamation(Integer idReclamation);
    boolean existsByReclamation_IdReclamation(Integer idReclamation);
    void deleteByReclamationIdReclamation(Integer idReclamation);
}
