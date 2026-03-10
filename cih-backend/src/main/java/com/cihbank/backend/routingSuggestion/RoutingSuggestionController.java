package com.cihbank.backend.routingSuggestion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routing")
public class RoutingSuggestionController {

    private final RoutingSuggestionService routingSuggestionService;

    public RoutingSuggestionController(RoutingSuggestionService routingSuggestionService) {
        this.routingSuggestionService = routingSuggestionService;
    }

    @GetMapping("/reclamation/{id}")
    public RoutingSuggestion getSuggestion(@PathVariable Integer id){
        return routingSuggestionService.getSuggestion(id);
    }
}