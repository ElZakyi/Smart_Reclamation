package com.cihbank.backend.plafondproposal;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plafond-proposal")
public class PlafondProposalController {

    private final PlafondProposalService service;

    public PlafondProposalController(PlafondProposalService service) {
        this.service = service;
    }

    @PostMapping("/request/{requestId}/user/{userId}")
    public PlafondProposal create(
            @PathVariable Integer requestId,
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> body
    ) {

        Double proposedLimit = Double.valueOf(body.get("proposedLimit").toString());
        String justification = body.get("justification").toString();

        return service.create(requestId, userId, proposedLimit, justification);
    }
    @GetMapping("/validation")
    public List<PlafondProposal> getForResponsable(){
        return service.getForResponsable();
    }
}
