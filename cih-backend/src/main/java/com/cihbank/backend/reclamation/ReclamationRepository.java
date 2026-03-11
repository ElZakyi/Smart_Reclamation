package com.cihbank.backend.reclamation;

import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReclamationRepository extends JpaRepository<Reclamation,Integer> {
    List<Reclamation> findByUserIdUser(Integer id);

    List<Reclamation> findByStatus(ReclamationStatus reclamationStatus);
}
