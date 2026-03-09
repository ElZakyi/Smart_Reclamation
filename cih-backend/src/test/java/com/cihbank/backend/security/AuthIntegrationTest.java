package com.cihbank.backend.security;

import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("admin@cih.com");
        user.setFullName("Admin");
        user.setPasswordHash(passwordEncoder.encode("1234"));
        user.setIsActive(true);
        user.setIsAccountLocked(false);
        user.setIsCredentialExpired(false);
        user.setIsAccountExpired(false);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    @Test
    void loginShouldReturnJwtToken() throws Exception{
        String requestBody = """
                {
                    "email": "admin@cih.com",
                    "password": "1234"
                }
                """;
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.notNullValue()));

    }
}
