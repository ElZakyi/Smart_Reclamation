package com.cihbank.backend.attachment;

import com.cihbank.backend.reclamation.Reclamation;
import com.cihbank.backend.reclamation.ReclamationRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AttachmentServiceTest {
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReclamationRepository reclamationRepository;
    @Autowired
    AttachmentService attachmentService;
    @Test
    void shouldUploadAttachment() throws IOException {
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
        attachment.setUser(savedUser);
        attachment.setReclamation(reclamation);
        attachment.setFileName("test.pdf");
        attachment.setFileType("application/pdf");
        attachment.setFileSize(100.0);
        attachment.setStorageUrl("uploads/test.pdf");
        attachment.setUploadedAt(LocalDateTime.now());
        Attachment attachmentSaved = attachmentRepository.save(attachment);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello".getBytes(StandardCharsets.UTF_8)
                );

        attachmentService.uploadattachment(savedUser.getIdUser(),reclamationSaved.getIdReclamation(),file);
        assertThat(attachmentRepository.findByReclamationIdReclamation(reclamationSaved.getIdReclamation())).isNotEmpty();
    }
    @Test
    void shouldGetAttachmentByReclamationId() throws IOException {
        User user = new User();
        user.setFullName("Saad");
        user.setEmail("saad@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        Reclamation reclamation = new Reclamation();
        reclamation.setUser(savedUser);
        reclamation.setTitle("Test3");
        reclamation.setDescription("Test3 description");
        reclamation.setStatus(ReclamationStatus.CREEE);
        Reclamation reclamationSaved = reclamationRepository.save(reclamation);

        Attachment attachment = new Attachment();
        attachment.setUser(savedUser);
        attachment.setReclamation(reclamation);
        attachment.setFileName("test.pdf");
        attachment.setFileType("application/pdf");
        attachment.setFileSize(100.0);
        attachment.setStorageUrl("uploads/test.pdf");
        attachment.setUploadedAt(LocalDateTime.now());

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.txt",
                        MediaType.TEXT_PLAIN_VALUE,
                        "Hello".getBytes(StandardCharsets.UTF_8)
                );

        attachmentService.uploadattachment(savedUser.getIdUser(),reclamationSaved.getIdReclamation(),file);
        List<Attachment> listAttachment = attachmentService.getAttachmentByReclamationId(reclamationSaved.getIdReclamation());
        assertThat(listAttachment).isNotEmpty();
        assertThat(listAttachment).hasSize(1);
    }
    @Test
    void shouldDeleteAttachment(){
        User user = new User();
        user.setFullName("timotet");
        user.setEmail("timotet@cih.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        User savedUser2 = userRepository.save(user);

        Reclamation reclamation2 = new Reclamation();
        reclamation2.setUser(savedUser2);
        reclamation2.setTitle("Test3");
        reclamation2.setDescription("Test3 description");
        reclamation2.setStatus(ReclamationStatus.CREEE);
        Reclamation reclamationSaved2 = reclamationRepository.save(reclamation2);

        Attachment attachment2 = new Attachment();
        attachment2.setUser(savedUser2);
        attachment2.setReclamation(reclamationSaved2);
        attachment2.setFileName("test.pdf");
        attachment2.setFileType("application/pdf");
        attachment2.setFileSize(100.0);
        attachment2.setStorageUrl("uploads/test.pdf");
        attachment2.setUploadedAt(LocalDateTime.now());
        Attachment attachmentSaved = attachmentRepository.save(attachment2);
        assertThat(attachmentService.getAttachmentByReclamationId(reclamationSaved2.getIdReclamation())).isNotEmpty();
        attachmentService.deleteAttachment(attachmentSaved.getIdAttachment());
        assertThat(attachmentService.getAttachmentByReclamationId(reclamationSaved2.getIdReclamation())).isEmpty();
    }
}
