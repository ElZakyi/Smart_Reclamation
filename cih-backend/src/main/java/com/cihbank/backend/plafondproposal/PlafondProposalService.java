package com.cihbank.backend.plafondproposal;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.plafondrequest.PlafondRequest;
import com.cihbank.backend.plafondrequest.PlafondRequestRepository;
import com.cihbank.backend.plafondrequest.PlafondRequestStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlafondProposalService {

    private final PlafondProposalRepository repository;
    private final PlafondRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public PlafondProposalService(
            PlafondProposalRepository repository,
            PlafondRequestRepository requestRepository,
            UserRepository userRepository,
            AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public PlafondProposal create(Integer requestId, Integer userId, Double proposedLimit, String justification) {

        PlafondRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Demande introuvable"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        PlafondProposal proposal = new PlafondProposal();
        proposal.setPlafondRequest(request);
        proposal.setUser(user);
        proposal.setProposedLimit(proposedLimit);
        proposal.setJustification(justification);
        proposal.setCreatedAt(LocalDateTime.now());

        // 🔥 update status
        request.setStatus(PlafondRequestStatus.EN_VALIDATION);

        requestRepository.save(request);

        PlafondProposal saved =  repository.save(proposal);
        auditLogService.log(AuditAction.CREATE_PLAFOND_PROPOSAL,"Proposition_plafond",saved.getIdPlafondProposal(),userId,null);
        return saved ;
    }
    public List<PlafondProposal> getForResponsable(){
        return repository.findByPlafondRequest_Status(PlafondRequestStatus.EN_VALIDATION); // ou filtré
    }
}
