package com.cihbank.backend.message;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ReclamationRepository reclamationRepository;
    private final AuditLogService auditLogService;
    public MessageService(MessageRepository messageRepository,AuditLogService auditLogService, ReclamationRepository reclamationRepository, UserRepository userRepository){
        this.messageRepository = messageRepository;
        this.reclamationRepository = reclamationRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }
    public List<Message> getMessagesByReclamation(Integer idReclamation){
        return messageRepository.findByReclamation_IdReclamationOrderByCreatedAtAsc(idReclamation);
    }
    public Message addMessage(Integer idUser, Integer idReclamation, String content, MessageType messageType){
        User foundUser = userRepository.findById(idUser).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        Reclamation foundReclamation = reclamationRepository.findById(idReclamation).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Reclamation introuvable !"));
        Message message = new Message();
        message.setUser(foundUser);
        message.setReclamation(foundReclamation);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setMessageType(messageType);
        Message saved = messageRepository.save(message);
        auditLogService.log(AuditAction.SEND_MESSAGE,"Message", saved.getIdMessage(), idUser,null);
        return saved;
    }
}
