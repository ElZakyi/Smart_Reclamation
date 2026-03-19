package com.cihbank.backend.attachment;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLog;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
public class AttachmentService {
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final ReclamationRepository reclamationRepository;
    private final AuditLogService auditLogService;
    public AttachmentService(AttachmentRepository attachmentRepository,AuditLogService auditLogService, UserRepository userRepository, ReclamationRepository reclamationRepository){
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.reclamationRepository = reclamationRepository;
        this.auditLogService = auditLogService;
    }
    @Transactional
    public void uploadattachment(Integer userId, Integer reclamationId, MultipartFile file) throws IOException {

        Reclamation reclamation = reclamationRepository.findById(reclamationId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Reclamation not found !"));
        if(!reclamation.getUser().getIdUser().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous pouvez pas utiliser une reclamation d'une autre personne pour enregistrer votre pièce jointe !");
        }
        if(reclamation.getStatus().equals(ReclamationStatus.CLOTUREE)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Reclamation est déja close !");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, fileName);
        Files.createDirectories(path.getParent());
        Files.write(path,file.getBytes());
        Attachment attachment = new Attachment();
        attachment.setReclamation(reclamation);
        attachment.setUser(user);
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize((double) file.getSize());
        attachment.setStorageUrl(path.toString());
        attachment.setUploadedAt(LocalDateTime.now());
        Attachment saved = attachmentRepository.save(attachment);
        auditLogService.log(AuditAction.UPLOAD_ATTACHMENT,"Attachement",saved.getIdAttachment(),userId,null);
    }
    public List<Attachment> getAttachmentByReclamationId(Integer idReclamation){
        return attachmentRepository.findByReclamationIdReclamation(idReclamation);
    }
    public void deleteAttachment(Integer idAttachment){
        attachmentRepository.deleteById(idAttachment);
        auditLogService.log(AuditAction.DELETE_ATTACHMENT,"Attachement",idAttachment,null,null);
    }
}
