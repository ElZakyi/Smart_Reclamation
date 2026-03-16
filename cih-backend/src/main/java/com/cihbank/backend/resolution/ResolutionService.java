package com.cihbank.backend.resolution;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ResolutionService {
    private final ReclamationRepository reclamationRepository;
    private final UserRepository userRepository;
    private final ResolutionRepository resolutionRepository;
    public ResolutionService(ReclamationRepository reclamationRepository, UserRepository userRepository, ResolutionRepository resolutionRepository){
        this.reclamationRepository = reclamationRepository;
        this.userRepository = userRepository;
        this.resolutionRepository = resolutionRepository;
    }
    public Resolution createResolution(Integer idReclamation , Integer idUser, String content){
        Reclamation reclamation = reclamationRepository.findById(idReclamation).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Réclamation introuvable !"));
        User agent = userRepository.findById(idUser).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        Resolution resolution = new Resolution();
        resolution.setUser(agent);
        resolution.setReclamation(reclamation);
        resolution.setCreatedAt(LocalDateTime.now());
        resolution.setContent(content);
        resolutionRepository.save(resolution);
        reclamation.setStatus(ReclamationStatus.RESOLUE);
        reclamationRepository.save(reclamation);
        return resolution;
    }
}
