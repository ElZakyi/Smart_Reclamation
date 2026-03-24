package com.cihbank.backend.plafondrequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlafondRequestRepository extends JpaRepository<PlafondRequest, Long> {
    List<PlafondRequest> findByTeamIdTeam(Integer idTeam);
    List<PlafondRequest> findByUser_IdUser(Integer idUser);

}