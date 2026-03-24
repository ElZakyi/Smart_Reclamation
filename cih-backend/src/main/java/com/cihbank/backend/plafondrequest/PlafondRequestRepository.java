package com.cihbank.backend.plafondrequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlafondRequestRepository extends JpaRepository<PlafondRequest, Integer> {
    List<PlafondRequest> findByTeamIdTeamAndStatus(Integer idTeam, PlafondRequestStatus status);
    List<PlafondRequest> findByUser_IdUser(Integer idUser);

}