package com.cihbank.backend.decision;

import com.cihbank.backend.decisionproposal.DecisionProposal;
import com.cihbank.backend.decisionproposal.DecisionProposalRepository;
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
public class DecisionService {
    private final DecisionRepository decisionRepository;
    private final ReclamationRepository reclamationRepository;
    private final UserRepository userRepository;
    private final DecisionProposalRepository decisionProposalRepository;
    public DecisionService(DecisionRepository decisionRepository,ReclamationRepository reclamationRepository,UserRepository userRepository, DecisionProposalRepository decisionProposalRepository){
        this.decisionRepository=decisionRepository;
        this.reclamationRepository = reclamationRepository;
        this.userRepository = userRepository;
        this.decisionProposalRepository = decisionProposalRepository;
    }
    public void acceptDecision(Integer idReclamation, Integer idDecisionProposal, Integer idUser, String motif){
        Reclamation reclamation = reclamationRepository.findById(idReclamation).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Réclamation introuvable !"));
        reclamation.setStatus(ReclamationStatus.CLOTUREE);
        Reclamation reclamationSaved = reclamationRepository.save(reclamation);
        User responsable = userRepository.findById(idUser).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        DecisionProposal decisionProposal = decisionProposalRepository.findById(idDecisionProposal).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Proposition introuvable !"));
        decisionProposal.setActive(false);
        decisionProposalRepository.save(decisionProposal);
        Decision decision = new Decision();
        decision.setDecisionType(decisionProposal.getType());
        decision.setMotif(motif);
        decision.setUser(responsable);
        decision.setReclamation(reclamationSaved);
        decision.setDecidedAt(LocalDateTime.now());
        decision.setOutcome(DecisionOutcome.VALIDE);
        decisionRepository.save(decision);
    }
    public void rejectDecision(Integer idReclamation,Integer idDecisionProposal, Integer idUser,String motif){
        Reclamation reclamation = reclamationRepository.findById(idReclamation).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Réclamation introuvable !"));
        reclamation.setStatus(ReclamationStatus.AFFECTEE);
        Reclamation reclamationSaved = reclamationRepository.save(reclamation);
        User responsable = userRepository.findById(idUser).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        DecisionProposal decisionProposal = decisionProposalRepository.findById(idDecisionProposal).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Proposition introuvable !"));
        Decision decision = new Decision();
        decision.setDecisionType(decisionProposal.getType());
        decision.setMotif(motif);
        decision.setUser(responsable);
        decision.setReclamation(reclamationSaved);
        decision.setDecidedAt(LocalDateTime.now());
        decision.setOutcome(DecisionOutcome.REFUSE);
        decisionRepository.save(decision);
    }
}
