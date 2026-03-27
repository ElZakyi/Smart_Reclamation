package com.cihbank.backend.plafonddecision;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/plafond-decision")
public class PlafondDecisionController {

    private final PlafondDecisionService service;

    public PlafondDecisionController(PlafondDecisionService service) {
        this.service = service;
    }

    @PostMapping("/request/{requestId}/user/{userId}")
    public String decide(
            @PathVariable Integer requestId,
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> body
    ) {

        String outcome = body.get("outcome").toString();
        String motif = body.get("motif").toString();

        service.decide(requestId, userId, outcome, motif);

        return "Décision enregistrée";
    }
}
