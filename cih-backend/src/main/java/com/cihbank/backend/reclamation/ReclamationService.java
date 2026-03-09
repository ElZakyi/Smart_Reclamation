package com.cihbank.backend.reclamation;

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
import java.util.UUID;

@Service
public class ReclamationService {
    private final ReclamationRepository reclamationRepository;
    private final UserRepository userRepository;
    public ReclamationService(ReclamationRepository reclamationRepository, UserRepository userRepository){
        this.reclamationRepository = reclamationRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public Reclamation createReclamation(Integer userId, Reclamation reclamation){
        User user = userRepository.findById(userId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found !"));
        reclamation.setUser(user);
        reclamation.setReference("REC-" + UUID.randomUUID().toString().substring(0,8));
        reclamation.setStatus(ReclamationStatus.CREEE);
        reclamation.setCreatedAt(LocalDateTime.now());
        return reclamationRepository.save(reclamation);
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
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not allowed to delete this reclamation !");
        }
        if(reclamation.getStatus() == null || !reclamation.getStatus().toString().equals("CREEE")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only created reclamation can be delete !");
        }
        reclamationRepository.delete(reclamation);
    }
    @Transactional
    public void updateReclamation(Integer idReclamation , Integer idUser, Reclamation reclamation){
        Reclamation reclamationToUpdate = reclamationRepository.findById(idReclamation).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reclamation not found !"));
        if(reclamationToUpdate.getUser().getIdUser() == null || !reclamationToUpdate.getUser().getIdUser().equals(idUser)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not allowed to delete this reclamation !");
        }
        if(reclamationToUpdate.getStatus() == null || reclamationToUpdate.getStatus() != ReclamationStatus.CREEE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only created reclamation can be delete !");
        }
        reclamationToUpdate.setTitle(reclamation.getTitle());
        reclamationToUpdate.setDescription(reclamation.getDescription());
        reclamationToUpdate.setType(reclamation.getType());
        reclamationToUpdate.setCanal(reclamation.getCanal());
        reclamationToUpdate.setPriority(reclamation.getPriority());
        reclamationRepository.save(reclamationToUpdate);
    }
}
