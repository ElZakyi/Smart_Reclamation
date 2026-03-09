package com.cihbank.backend.reclamation;

import com.cihbank.backend.reclamation.enums.CanalType;
import com.cihbank.backend.reclamation.enums.PriorityLevel;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
import com.cihbank.backend.reclamation.enums.ReclamationType;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ReclamationRepositoryTest {
    @Autowired
    ReclamationRepository reclamationRepository;
    @Autowired
    UserRepository userRepository;
    @Test
    void shouldSaveReclamation(){
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        Reclamation rec = new Reclamation();
        rec.setUser(user);
        rec.setTitle("Test Title");
        rec.setType(ReclamationType.AUTRE);
        rec.setDescription("Test description");
        rec.setCanal(CanalType.AUTRE);
        rec.setStatus(ReclamationStatus.CREEE);
        rec.setPriority(PriorityLevel.LOW);
        rec.setCreatedAt(LocalDateTime.now());
        rec.setUpdatedAt(LocalDateTime.now());
        Reclamation reclamation = reclamationRepository.save(rec);
        assertThat(reclamation.getTitle()).isEqualTo("Test Title");
        assertThat(reclamation.getDescription()).isEqualTo("Test description");
    }
    @Test
    void shouldReturnReclamationByIdUser(){
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("1234");
        user.setPasswordHash("DummyHash");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        Reclamation rec = new Reclamation();
        rec.setUser(user);
        rec.setTitle("Test Title");
        rec.setType(ReclamationType.AUTRE);
        rec.setDescription("Test description");
        rec.setCanal(CanalType.AUTRE);
        rec.setStatus(ReclamationStatus.CREEE);
        rec.setPriority(PriorityLevel.LOW);
        rec.setCreatedAt(LocalDateTime.now());
        rec.setUpdatedAt(LocalDateTime.now());
        Reclamation reclamation = reclamationRepository.save(rec);
        List<Reclamation> newReclamation = reclamationRepository.findByUserIdUser(user.getIdUser());
        assertThat(newReclamation).isNotEmpty();
        assertThat(newReclamation.get(0).getTitle()).isEqualTo("Test Title");
    }
}
