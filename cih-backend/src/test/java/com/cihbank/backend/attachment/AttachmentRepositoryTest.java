package com.cihbank.backend.attachment;

import com.cihbank.backend.attachment.Attachment;
import com.cihbank.backend.attachment.AttachmentRepository;
import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class AttachmentRepositoryTest {
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    ReclamationRepository reclamationRepository;
    @Autowired
    UserRepository userRepository;
    @Test
    void shouldGetAllAttachmentsByReclamation(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zak@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);
        Reclamation reclamation = new Reclamation();
        reclamation.setUser(savedUser);
        reclamation.setTitle("Test");
        reclamation.setDescription("Test description");
        reclamation.setStatus(ReclamationStatus.CREEE);
        Reclamation reclamationSaved = reclamationRepository.save(reclamation);
        Attachment attachment = new Attachment();
        attachment.setUser(user);
        attachment.setReclamation(reclamation);
        attachment.setFileName("test.pdf");
        attachment.setFileType("application/pdf");
        attachment.setFileSize(100.0);
        attachment.setStorageUrl("uploads/test.pdf");
        attachment.setUploadedAt(LocalDateTime.now());
        Attachment attachmentSaved = attachmentRepository.save(attachment);
        List<Attachment> listAttachment = attachmentRepository.findByReclamationIdReclamation(reclamationSaved.getIdReclamation());
        assertThat(listAttachment).isNotEmpty();
        assertThat(listAttachment).hasSize(1);

    }
    @Test
    void shouldReturnNoAttachment(){
        User user = new User();
        user.setFullName("Safaa");
        user.setEmail("safaa@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyyHash");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);
        Reclamation reclamation = new Reclamation();
        reclamation.setUser(savedUser);
        reclamation.setTitle("Test2");
        reclamation.setDescription("Test2 description");
        reclamation.setStatus(ReclamationStatus.CREEE);
        Reclamation reclamationSaved = reclamationRepository.save(reclamation);
        List<Attachment> listAttachment = attachmentRepository.findByReclamationIdReclamation(reclamationSaved.getIdReclamation());
        assertThat(listAttachment).isEmpty();
    }

}
