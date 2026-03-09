package com.cihbank.backend.ai;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/classification")
public class ClassificationResultController {
    private final ClassificationResultService classificationResultService;
    public ClassificationResultController(ClassificationResultService classificationResultService){
        this.classificationResultService = classificationResultService;
    }
    // Assisté par IA : retourne suggestion sans enregistrer
    @PostMapping("/preview")
    public Map<String, Object> preview(@RequestBody Map<String, Object> playload){
        String description = (String) playload.get("description");
        return classificationResultService.previewClassification(description);
    }
    // Pour consulter le résultat sauvegardé lié à une réclamation
    @GetMapping("/reclamation/{idReclamation}")
    public ClassificationResult getByReclamation(@PathVariable Integer idReclamation) {
        return classificationResultService.getByReclamation(idReclamation);
    }
}
