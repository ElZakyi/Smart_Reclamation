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
    @PostMapping("/accept/{idSuggestion}/agent/{idAgent}")
    public ReclamationAssignment acceptSuggestion(
            @PathVariable Integer idSuggestion,
            @PathVariable Integer idAgent){

        return assignmentService.acceptSuggestion(idSuggestion,idAgent);
    }
    @PostMapping("/reject/{idSuggestion}/agent/{idAgent}")
    public String rejectSuggestion(
            @PathVariable Integer idSuggestion,
            @PathVariable Integer idAgent){

        assignmentService.rejectSuggestion(idSuggestion,idAgent);

        return "Suggestion rejetée";
    }
}
