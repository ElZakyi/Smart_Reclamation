package com.cihbank.backend.reclamation;

import com.cihbank.backend.ai.AIClientService;
import com.cihbank.backend.ai.ClassificationResult;
import com.cihbank.backend.ai.ClassificationResultRepository;
import com.cihbank.backend.ai.ClassificationResultService;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReclamationService {
    private final ReclamationRepository reclamationRepository;
    private final UserRepository userRepository;
    private final AIClientService aiClientService;
    private final ClassificationResultService classificationResultService;
    private final ClassificationResultRepository classificationResultRepository;
    public ReclamationService(ReclamationRepository reclamationRepository, UserRepository userRepository, AIClientService aiClientService, ClassificationResultService classificationResultService, ClassificationResultRepository classificationResultRepository){
        this.reclamationRepository = reclamationRepository;
        this.userRepository = userRepository;
        this.aiClientService = aiClientService;
        this.classificationResultService = classificationResultService;
        this.classificationResultRepository = classificationResultRepository;
    }
    @Transactional
    public Reclamation createReclamation(Integer userId, Reclamation reclamation){
        User user = userRepository.findById(userId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found !"));
        Reclamation newReclamation = new Reclamation();
        newReclamation.setUser(user);
        newReclamation.setTitle(reclamation.getTitle());
        newReclamation.setDescription(reclamation.getDescription());
        newReclamation.setType(reclamation.getType());
        newReclamation.setCanal(reclamation.getCanal());
        newReclamation.setPriority(reclamation.getPriority());
        String reference = "REC-" + System.currentTimeMillis();
        newReclamation.setReference(reference);
        newReclamation.setStatus(ReclamationStatus.CREEE);
        newReclamation.setCreatedAt(LocalDateTime.now());
        Reclamation saved = reclamationRepository.save(newReclamation);
        if(Boolean.TRUE.equals(reclamation.getIsAiAssisted())){
            Map<String,Object> aiResult = classificationResultService.previewClassification(saved.getDescription());
            classificationResultService.saveForReclamation(
                    saved.getIdReclamation(),
                    aiResult
            );
        }
        return reclamationRepository.save(newReclamation);
    }
    public List<Reclamation> getAllReclamations(){
        return reclamationRepository.findAll();
    }
    public List<Reclamation> getReclamationsByUser(Integer idUser){
        return reclamationRepository.findByUserIdUser(idUser);
    }
    public Reclamation getReclamationById(Integer idReclamation){
        return reclamationRepository.findById(idReclamation).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reclamation not found !"));
    }
    @Transactional
    public void deleteReclamation(Integer idReclamation,Integer idUser){
        Reclamation reclamation = reclamationRepository.findById(idReclamation).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reclamation not found !"));
        if(reclamation.getUser().getIdUser() == null || !reclamation.getUser().getIdUser().equals(idUser)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Vous n'avez pas le droit de supprimer cette réclamation !");
        }
        if(reclamation.getStatus() == null || !reclamation.getStatus().toString().equals("CREEE")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Seules les réclamations avec le statut CRÉÉES peuvent être supprimées !");
        }
        classificationResultRepository.deleteByReclamationIdReclamation(idReclamation);
        reclamationRepository.delete(reclamation);
    }
    @Transactional
    public void updateReclamation(Integer idReclamation , Integer idUser, Reclamation reclamation){
        Reclamation reclamationToUpdate = reclamationRepository.findById(idReclamation).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reclamation not found !"));
        if(reclamationToUpdate.getUser().getIdUser() == null || !reclamationToUpdate.getUser().getIdUser().equals(idUser)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Vous n'avez pas le droit de supprimer cette réclamation !");
        }
        if(reclamationToUpdate.getStatus() == null || reclamationToUpdate.getStatus() != ReclamationStatus.CREEE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Seules les réclamations avec le statut CRÉÉES peuvent être supprimées ! !");
        }
        reclamationToUpdate.setTitle(reclamation.getTitle());
        reclamationToUpdate.setDescription(reclamation.getDescription());
        reclamationToUpdate.setType(reclamation.getType());
        reclamationToUpdate.setCanal(reclamation.getCanal());
        reclamationToUpdate.setPriority(reclamation.getPriority());
        reclamationRepository.save(reclamationToUpdate);
    }
}
