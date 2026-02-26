package com.cihbank.backend.reclamation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReclamationRepository extends JpaRepository<Reclamation,Integer> {
    List<Reclamation> findByUserIdUser(Integer id);
}
