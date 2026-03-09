package com.cihbank.backend.message;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;
    public MessageController(MessageService messageService){
        this.messageService = messageService;
    }
    @GetMapping("/reclamation/{idReclamation}")
    public List<Message> getMessagesByReclamation(@PathVariable Integer idReclamation){
        return messageService.getMessagesByReclamation(idReclamation);
    }
    @PostMapping
    public Message addMessage(@RequestBody Map<String, Object> payload) {

        Integer idUser = Integer.valueOf(payload.get("idUser").toString());
        Integer idReclamation = Integer.valueOf(payload.get("idReclamation").toString());
        String content = payload.get("content").toString();

        String typeStr = (String) payload.get("type");
        MessageType type = typeStr != null ? MessageType.valueOf(typeStr) : null;

        return messageService.addMessage(idUser, idReclamation, content, type);
    }

}
