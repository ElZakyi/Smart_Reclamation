package com.cihbank.backend.routingSuggestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoutingSuggestionRepository extends JpaRepository<RoutingSuggestion,Integer> {
    Optional<RoutingSuggestion> findByReclamationIdReclamation(Integer idReclamation);
    void deleteByReclamationIdReclamation(Integer idReclamation);
    Optional<RoutingSuggestion> findByReclamationIdReclamationAndRoutingStatus(
            Integer idReclamation,
            RoutingStatus status
    );
    List<RoutingSuggestion> findByRoutingStatus(RoutingStatus routingStatus);
}
