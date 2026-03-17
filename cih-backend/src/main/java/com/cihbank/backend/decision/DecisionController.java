package com.cihbank.backend.decision;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decision")
public class DecisionController {
    private final DecisionService decisionService;
    public DecisionController(DecisionService decisionService){
        this.decisionService = decisionService;
    }
    @PostMapping("accept/reclamation/{idReclamation}/user/{idUser}/proposal/{idProposal}")
    public String acceptDecision(@PathVariable Integer idReclamation, @PathVariable Integer idUser,@PathVariable Integer idProposal, @RequestBody String motif){
        decisionService.acceptDecision(idReclamation,idProposal,idUser,motif);
        return "Décision à été accéptée ! ";
    }
    @PostMapping("reject/reclamation/{idReclamation}/user/{idUser}/proposal/{idProposal}")
    public String rejectDecision(@PathVariable Integer idReclamation, @PathVariable Integer idUser,@PathVariable Integer idProposal, @RequestBody String motif){
        decisionService.rejectDecision(idReclamation,idProposal,idUser,motif);
        return "Décision à été accéptée ! ";
    }
}
