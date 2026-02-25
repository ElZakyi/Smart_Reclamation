package com.cihbank.backend.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Test
    void shouldCreateUser(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zakaria@mail.com");
        user.setPasswordHash("123");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getIdUser()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("zakaria@mail.com");


    }
}
