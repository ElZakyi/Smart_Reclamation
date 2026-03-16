package com.cihbank.backend.resolution;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resolution")
public class ResolutionController {
    private final ResolutionService resolutionService;
    public ResolutionController(ResolutionService resolutionService){
        this.resolutionService = resolutionService;
    }
    @PostMapping("/reclamation/{idReclamation}/user/{idUser}")
    public Resolution createResolution(@PathVariable Integer idReclamation, @PathVariable Integer idUser, @RequestBody String content){
        return resolutionService.createResolution(idReclamation,idUser,content);
    }
}
