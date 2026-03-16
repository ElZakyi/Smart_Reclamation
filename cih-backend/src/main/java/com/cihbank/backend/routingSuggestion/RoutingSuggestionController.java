package com.cihbank.backend.routingSuggestion;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routing")
public class RoutingSuggestionController {

    private final RoutingSuggestionService routingSuggestionService;
    private final RoutingSuggestionRepository routingSuggestionRepository;

    public RoutingSuggestionController(RoutingSuggestionService routingSuggestionService, RoutingSuggestionRepository routingSuggestionRepository) {
        this.routingSuggestionService = routingSuggestionService;
        this.routingSuggestionRepository = routingSuggestionRepository;
    }

    @GetMapping("/reclamation/{id}")
    public RoutingSuggestion getSuggestion(@PathVariable Integer id){
        return routingSuggestionService.getSuggestion(id);
    }
    @GetMapping("/pending")
    public List<RoutingSuggestion> getPendingSuggestions(){
        return routingSuggestionRepository.findByRoutingStatus(RoutingStatus.PENDING);
    }
}