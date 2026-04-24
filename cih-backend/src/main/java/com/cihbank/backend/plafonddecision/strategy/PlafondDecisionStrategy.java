package com.cihbank.backend.plafonddecision.strategy;

import com.cihbank.backend.plafondproposal.PlafondProposal;
import com.cihbank.backend.plafondrequest.PlafondRequest;
import com.cihbank.backend.plafonddecision.PlafondDecision;

public interface PlafondDecisionStrategy {

    void execute(
            PlafondProposal proposal,
            PlafondRequest request,
            PlafondDecision decision,
            Integer proposalId,
            Integer userId
    );
}