package com.cihbank.backend.message;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationService;
import com.cihbank.backend.reclamation.enums.PriorityLevel;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class MessageServiceTest {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    ReclamationService reclamationService;
    @Test
    void shouldAddMessage(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zakaria@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("dummy");
        user.setIsActive(true);
        User userSaved = userService.createUser(user);
        Reclamation reclamation = new Reclamation();
        reclamation.setUser(userSaved);
        reclamation.setTitle("Test");
        reclamation.setDescription("Test description");
        reclamation.setStatus(ReclamationStatus.CREEE);
        reclamation.setPriority(PriorityLevel.HIGH);
        reclamation.setCreatedAt(LocalDateTime.now());
        Reclamation savedReclamation = reclamationService.createReclamation(userSaved.getIdUser(),reclamation);
        Message message = messageService.addMessage(userSaved.getIdUser(),savedReclamation.getIdReclamation(),"Hello World",MessageType.PROVIDE_INFO);
        Assertions.assertThat(message.getMessageType()).isEqualTo(MessageType.PROVIDE_INFO);
        Assertions.assertThat(message.getContent()).isEqualTo("Hello World");
        Assertions.assertThat(message.getUser().getFullName()).isEqualTo("Zakaria");
        Assertions.assertThat(message.getReclamation().getTitle()).isEqualTo("Test");
    }
    @Test
    void shouldReturnMessagesOfReclamation(){
        User user = new User();
        user.setFullName("Saad");
        user.setEmail("saad@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("dummy");
        user.setIsActive(true);
        User userSaved = userService.createUser(user);
        Reclamation reclamation = new Reclamation();
        reclamation.setUser(userSaved);
        reclamation.setTitle("Test");
        reclamation.setDescription("Test description");
        reclamation.setStatus(ReclamationStatus.CREEE);
        reclamation.setPriority(PriorityLevel.HIGH);
        reclamation.setCreatedAt(LocalDateTime.now());
        Reclamation savedReclamation = reclamationService.createReclamation(userSaved.getIdUser(),reclamation);
        messageService.addMessage(userSaved.getIdUser(),savedReclamation.getIdReclamation(),"Hello Wolrd",MessageType.PROVIDE_INFO);
        List<Message> listMessage = messageService.getMessagesByReclamation(reclamation.getIdReclamation());
        Assertions.assertThat(listMessage).isNotEmpty();
        Assertions.assertThat(listMessage).hasSize(1);
    }
}
