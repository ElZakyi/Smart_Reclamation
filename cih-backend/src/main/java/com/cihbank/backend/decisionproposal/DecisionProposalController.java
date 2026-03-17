package com.cihbank.backend.decisionproposal;

import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decision-proposal")
public class DecisionProposalController {
    private final DecisionProposalService decisionProposalService;
    public DecisionProposalController(DecisionProposalService decisionProposalService){
        this.decisionProposalService = decisionProposalService;
    }
    @PostMapping("/reclamation/{idReclamation}/user/{idUser}")
    public DecisionProposal createProposal(@PathVariable Integer idReclamation, @PathVariable Integer idUser, @RequestParam DecisionType decisionType, @RequestBody String justification){
        return decisionProposalService.createProposition(idReclamation,decisionType,idUser,justification);
    }
    @GetMapping("/decision")
    public List<DecisionProposal> getDecisionProposals(){
        return decisionProposalService.findByIsActiveTrueAndReclamationStatus();
    }
}
