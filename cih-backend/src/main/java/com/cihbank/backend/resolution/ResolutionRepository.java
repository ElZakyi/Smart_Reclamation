package com.cihbank.backend.resolution;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResolutionRepository extends JpaRepository<Resolution,Integer> {
    List<Resolution> findByReclamationIdReclamation(Integer idReclamation);
}
