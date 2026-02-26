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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ReclamationServiceTest {
    @Autowired
    ReclamationRepository reclamationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ReclamationService reclamationService;
    @Test
    void shouldCreateReclamation(){
        User user = new User();
        user.setFullName("Test User");
        user.setPassword("1234");
        user.setPasswordHash("Dummy");
        user.setEmail("test@test.com");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);
        Reclamation rec = new Reclamation();
        rec.setTitle("Paiement refusé");
        rec.setType(ReclamationType.PAIEMENT_REFUSE);
        rec.setDescription("Test description");
        rec.setCanal(CanalType.E_COMMERCE);
        rec.setPriority(PriorityLevel.HIGH);
        Reclamation reclamationSaved = reclamationService.createReclamation(user.getIdUser(),rec);
        assertThat(reclamationSaved.getIdReclamation()).isNotNull();
        assertThat(reclamationSaved.getTitle()).isEqualTo("Paiement refusé");
        assertThat(reclamationSaved.getStatus()).isEqualTo(ReclamationStatus.CREEE);

    }
    @Test
    void shouldReturnAllReclamation(){
        User user = new User();
        user.setFullName("Test User");
        user.setPassword("1234");
        user.setPasswordHash("Dummy");
        user.setEmail("test@cih.com");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        Reclamation rec = new Reclamation();
        rec.setTitle("Paiement refusé");
        rec.setType(ReclamationType.PAIEMENT_REFUSE);
        rec.setDescription("Test description");
        rec.setCanal(CanalType.E_COMMERCE);
        rec.setPriority(PriorityLevel.HIGH);
        Reclamation rec2 = new Reclamation();
        rec2.setTitle("Erreur Ecommerce");
        rec2.setType(ReclamationType.CARTE_BLOQUEE);
        rec2.setDescription("Test 2");
        rec2.setCanal(CanalType.E_COMMERCE);
        rec2.setPriority(PriorityLevel.HIGH);
        Reclamation reclamationSaved1 = reclamationService.createReclamation(user.getIdUser(),rec);
        Reclamation reclamationSaved2 = reclamationService.createReclamation(user.getIdUser(),rec2);
        List<Reclamation> listReclamation = reclamationService.getAllReclamations();
        assertThat(listReclamation).isNotEmpty();
        assertThat(listReclamation).hasSize(4);
    }
    @Test
    void shouldReturnReclamationByUserId(){
        User user = new User();
        user.setFullName("Zak User");
        user.setPassword("1234");
        user.setPasswordHash("Dummy");
        user.setEmail("zak@cih.com");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        Reclamation rec = new Reclamation();
        rec.setTitle("Paiement refusé");
        rec.setType(ReclamationType.PAIEMENT_REFUSE);
        rec.setDescription("Test description");
        rec.setCanal(CanalType.E_COMMERCE);
        rec.setPriority(PriorityLevel.HIGH);
        Reclamation reclamationSaved = reclamationService.createReclamation(user.getIdUser(),rec);
        List<Reclamation> reclamationList = reclamationService.getReclamationsByUser(user.getIdUser());
        assertThat(reclamationList).isNotEmpty();
        assertThat(reclamationList).hasSize(1);
        assertThat(reclamationList.get(0).getTitle()).isEqualTo("Paiement refusé");
    }
    @Test
    void shouldReturnReclamationById(){
        User user = new User();
        user.setFullName("Ismail User");
        user.setPassword("1234");
        user.setPasswordHash("Dummy");
        user.setEmail("Isamail@cih.com");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        Reclamation rec = new Reclamation();
        rec.setTitle("Paiement is refused");
        rec.setType(ReclamationType.PAIEMENT_REFUSE);
        rec.setDescription("Test de description");
        rec.setCanal(CanalType.E_COMMERCE);
        rec.setPriority(PriorityLevel.HIGH);
        Reclamation reclamationSaved = reclamationService.createReclamation(user.getIdUser(),rec);
        Reclamation reclamationRecupere = reclamationService.getReclamationById(reclamationSaved.getIdReclamation());
        assertThat(reclamationRecupere.getIdReclamation()).isNotNull();
        assertThat(reclamationRecupere.getTitle()).isEqualTo("Paiement is refused");
        assertThat(reclamationRecupere.getDescription()).isEqualTo("Test de description");
    }
}
