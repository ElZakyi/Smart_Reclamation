package com.cihbank.backend.plafondproposal;

import com.cihbank.backend.plafondrequest.PlafondRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlafondProposalRepository extends JpaRepository<PlafondProposal, Integer> {
    List<PlafondProposal> findByPlafondRequest_Status(PlafondRequestStatus plafondRequestStatus);
}
