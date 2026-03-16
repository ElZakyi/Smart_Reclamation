package com.cihbank.backend.reclamationassignment;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignment")
public class ReclamationAssignmentController {
    private final ReclamationAssignmentService assignmentService;

    public ReclamationAssignmentController(ReclamationAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }
    @PostMapping("/accept/{idSuggestion}/responsable/{idResponsable}")
    public ReclamationAssignment acceptSuggestion(
            @PathVariable Integer idSuggestion,
            @PathVariable Integer idResponsable){

        return assignmentService.acceptSuggestion(idSuggestion,idResponsable);
    }
    @PostMapping("/reject/{idSuggestion}/responsable/{idResponsable}")
    public String rejectSuggestion(
            @PathVariable Integer idSuggestion,
            @PathVariable Integer idResponsable){

        assignmentService.rejectSuggestion(idSuggestion, idResponsable);

        return "Suggestion rejetée";
    }
    @PostMapping("/manual/{idSuggestion}/responsable/{idResponsable}")
    public ReclamationAssignment manualAssignment(@PathVariable Integer idSuggestion, @PathVariable Integer idResponsable, @RequestParam Integer idTeam, @RequestParam Integer idAgent){
        return assignmentService.manualAssignment(idSuggestion,idResponsable,idTeam,idAgent);
    }
    @GetMapping("/agent/{idAgent}")
    public List<ReclamationAssignment> getAgentAssignments(
            @PathVariable Integer idAgent){
        return assignmentService.getAssignmentsForAgent(idAgent);
    }
}
