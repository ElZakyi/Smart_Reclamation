package com.cihbank.backend.ai;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.CanalType;
import com.cihbank.backend.reclamation.enums.ReclamationType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ClassificationResultService {
    private final AIClientService aiClientService;
    private final ClassificationResultRepository classificationResultRepository;
    private final ReclamationRepository reclamationRepository;
    private final AuditLogService auditLogService;
    public ClassificationResultService(AIClientService aiClientService, AuditLogService auditLogService, ClassificationResultRepository classificationResultRepository, ReclamationRepository reclamationRepository){
        this.aiClientService = aiClientService;
        this.classificationResultRepository = classificationResultRepository;
        this.reclamationRepository = reclamationRepository;
        this.auditLogService = auditLogService;
    }
    public Map<String, Object> previewClassification(String description){
        if(description == null || description.trim().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Description is required !");
        }
        return aiClientService.classifyDescription(description);
    }
    public ClassificationResult saveForReclamation(Integer idReclamation, Map<String, Object> aiResult){
        Reclamation reclamation = reclamationRepository.findById(idReclamation).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reclamation not found !"));
        // si déjà exist -> on peut soit update, soit refuser.
        reclamation.setIsAiAssisted(true);
        reclamationRepository.save(reclamation);
        ClassificationResult cr;
        if (classificationResultRepository.existsByReclamation_IdReclamation(idReclamation)) {
            cr = classificationResultRepository.findByReclamation_IdReclamation(idReclamation)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Classification non trouvée !"));
        } else {
            cr = new ClassificationResult();
            cr.setReclamation(reclamation);
            cr.setCreatedAt(LocalDateTime.now());
        }
        cr.setPredictedType(ReclamationType.valueOf((String) aiResult.get("predictedType")));
        cr.setPredictedCanal(CanalType.valueOf((String) aiResult.get("predictedCanal")));
        cr.setSuggestedTitle((String) aiResult.get("suggestedTitle"));
        Object confObj = aiResult.get("confidenceScore");
        Double confidence = confObj instanceof Number ? ((Number) confObj).doubleValue() : 0.0;
        cr.setConfidenceScore(confidence);
        cr.setModelVersion((String) aiResult.get("modelVersion"));
        ClassificationResult saved = classificationResultRepository.save(cr);
        auditLogService.log(AuditAction.AI_CLASSIFICATION,"Réclamation",idReclamation,saved.getIdClassification(),null);
        return saved;
    }
    public ClassificationResult getByReclamation(Integer idReclamation) {
        return classificationResultRepository.findByReclamation_IdReclamation(idReclamation)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ClassificationResult non trouvée !"));
    }

}
