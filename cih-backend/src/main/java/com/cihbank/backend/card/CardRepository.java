package com.cihbank.backend.card;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card,Integer> {
    List<Card> findByUser_IdUser(Integer idUser);
}
