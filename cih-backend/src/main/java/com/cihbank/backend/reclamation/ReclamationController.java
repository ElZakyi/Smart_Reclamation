package com.cihbank.backend.reclamation;

import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations")
public class ReclamationController {
    private final ReclamationService reclamationService;
    public ReclamationController(ReclamationService reclamationService){
        this.reclamationService = reclamationService;
    }
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('CREATE_RECLAMATION')")
    public String createReclamation(@PathVariable Integer userId, @RequestBody Reclamation reclamation){
        reclamationService.createReclamation(userId,reclamation);
        return "Reclamation crée avec succés !";
    }
    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_RECLAMATION')")
    public List<Reclamation> getAllReclamation(){
        return reclamationService.getAllReclamations();
    }
    @GetMapping("{idReclamation}")
    @PreAuthorize("hasAuthority('VIEW_RECLAMATION')")
    public Reclamation getReclamation(@PathVariable Integer idReclamation){
        return reclamationService.getReclamationById(idReclamation);
    }
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('VIEW_RECLAMATION')")
    public List<Reclamation> getReclamationsByUserId(@PathVariable Integer userId){
        return reclamationService.getReclamationsByUser(userId);
    }
    @GetMapping("/pending")
    public List<Reclamation> getPendingReclamations(){
        return reclamationService.getPendingReclamations();
    }
    @DeleteMapping("/{idReclamation}/user/{idUser}")
    @PreAuthorize("hasAuthority('DELETE_RECLAMATION')")
    public String deleteReclamation(@PathVariable Integer idReclamation,@PathVariable Integer idUser){
        reclamationService.deleteReclamation(idReclamation,idUser);
        return "Reclamation supprimée avec succés !";
    }
    @PutMapping("/{idReclamation}/user/{idUser}")
    @PreAuthorize("hasAuthority('UPDATE_RECLAMATION')")
    public String updateReclamation(@PathVariable Integer idReclamation, @PathVariable Integer idUser,@RequestBody Reclamation reclamation){
        reclamationService.updateReclamation(idReclamation,idUser,reclamation);
        return "Reclamation a été mise à jour avec succés !";
    }
    @GetMapping("/decision")
    public List<Reclamation> getReclamationForDecision(){
        return reclamationService.getReclamationForDecision();
    }

}
