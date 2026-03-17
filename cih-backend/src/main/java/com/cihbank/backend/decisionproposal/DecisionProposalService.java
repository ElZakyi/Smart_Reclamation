package com.cihbank.backend.decisionproposal;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DecisionProposalService {
    private final DecisionProposalRepository decisionProposalRepository;
    private final ReclamationRepository reclamationRepository;
    private final UserRepository userRepository;
    public DecisionProposalService(DecisionProposalRepository decisionProposalRepository, ReclamationRepository reclamationRepository, UserRepository userRepository){
        this.decisionProposalRepository = decisionProposalRepository;
        this.reclamationRepository = reclamationRepository;
        this.userRepository = userRepository;
    }
    public DecisionProposal createProposition(Integer idReclamation,DecisionType type, Integer idUser, String justification){
        Reclamation reclamation = reclamationRepository.findById(idReclamation).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Réclamation introuvable !"));
        reclamation.setStatus(ReclamationStatus.EN_VALIDATION);
        reclamationRepository.save(reclamation);
        User agent = userRepository.findById(idUser).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        DecisionProposal decisionProposal = new DecisionProposal();
        decisionProposal.setUser(agent);
        decisionProposal.setReclamation(reclamation);
        decisionProposal.setJustification(justification);
        decisionProposal.setType(type);
        decisionProposal.setActive(true);
        decisionProposal.setCreatedAt(LocalDateTime.now());
        return decisionProposalRepository.save(decisionProposal);
    }
    public List<DecisionProposal> findByIsActiveTrueAndReclamationStatus(){
        return decisionProposalRepository.findByIsActiveTrueAndReclamation_Status(ReclamationStatus.EN_VALIDATION);
    }
}
