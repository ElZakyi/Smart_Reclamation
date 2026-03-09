package com.cihbank.backend.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Integer> {
    List<Message> findByReclamation_IdReclamationOrderByCreatedAtAsc(Integer idReclamation);
}
