package com.cihbank.backend.reclamationassignment;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
