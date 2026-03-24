package com.cihbank.backend.plafondrequest;

import com.cihbank.backend.card.Card;
import com.cihbank.backend.card.CardRepository;
import com.cihbank.backend.team.Team;
import com.cihbank.backend.team.TeamRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlafondRequestService {

    private final PlafondRequestRepository repository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final TeamRepository teamRepository;

    public PlafondRequestService(
            PlafondRequestRepository repository,
            UserRepository userRepository,
            CardRepository cardRepository,
            TeamRepository teamRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.teamRepository = teamRepository;
    }

    // =========================
    // CREATE
    // =========================
    public PlafondRequest create(Integer userId, Integer cardId, Double limit, String justification) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Carte introuvable !"));

        Team team = teamRepository.findByName("PLAFOND_TEAM")
                .orElseThrow(() -> new RuntimeException("Team plafond introuvable"));
        // 🔥 règle métier
        if (limit <= card.getCurrentLimit()) {
            throw new RuntimeException("Le nouveau plafond doit être supérieur au plafond actuel");
        }

        PlafondRequest request = new PlafondRequest();

        request.setUser(user);
        request.setCard(card);
        request.setRequestedLimit(limit);
        request.setJustification(justification);
        request.setStatus(PlafondRequestStatus.EN_ATTENTE);
        request.setCreatedAt(LocalDateTime.now());
        request.setTeam(team);

        return repository.save(request);
    }

    // =========================
    // GET ALL
    // =========================
    public List<PlafondRequest> getAll() {
        return repository.findAll();
    }

    // =========================
    // GET BY USER
    // =========================
    public List<PlafondRequest> getByUser(Integer idUser) {
        return repository.findByUser_IdUser(idUser);
    }

    // =========================
    // GET ONE
    // =========================
    public PlafondRequest getOne(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }
    public List<PlafondRequest> getByTeam(Integer idTeam){
        return repository.findByTeamIdTeam(idTeam);
    }
}