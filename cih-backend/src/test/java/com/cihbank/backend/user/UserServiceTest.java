package com.cihbank.backend.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Test
    void shouldCreateUser(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zak@mail.com");
        user.setPasswordHash("123");
        User userSaved = userService.createUser(user);
        assertThat(userSaved.getIdUser()).isNotNull();
        assertThat(userSaved.getIsActive()).isTrue();
        assertThat(userSaved.getCreatedAt()).isNotNull();
    }
    @Test
    void shouldReturnAllUsers(){
        User user1 = new User();
        user1.setFullName("Zakaria");
        user1.setEmail("zak@mail.com");
        user1.setPasswordHash("123");
        User user2 = new User();
        user2.setFullName("Safaa");
        user2.setEmail("safaa@mail.com");
        user2.setPasswordHash("321");

        userService.createUser(user1);
        userService.createUser(user2);

        List<User> userList = userService.getAllUsers();
        assertThat(userList).hasSize(2);
    }
    @Test
    void shouldActivateUser(){
        User user = new User();
        user.setFullName("Zak");
        user.setEmail("zak@cih.com");
        user.setPassword("1234");
        userService.createUser(user);
        user.setIsActive(false);
        assertThat(user.getIsActive()).isFalse();
        userService.activate(user.getIdUser());
        assertThat(user.getIsActive()).isTrue();
    }
    @Test
    void shouldDesactivateUser(){
        User user = new User();
        user.setFullName("Zak");
        user.setEmail("zak@cih.com");
        user.setPassword("1234");
        userService.createUser(user);
        assertThat(user.getIsActive()).isTrue();
        userService.desactivate(user.getIdUser());
        assertThat(user.getIsActive()).isFalse();
    }
    @Test
    void shouldUpdateUser(){
        User user = new User();
        user.setFullName("Zak");
        user.setEmail("zak@cih.com");
        user.setPassword("1234");
        userService.createUser(user);
        User userUpdate = new User();
        userUpdate.setFullName("Driss");
        userUpdate.setEmail("driss@cih.com");
        userUpdate.setPassword("456");
        userService.updateUser(user.getIdUser(),userUpdate);
        assertThat(user.getFullName()).isEqualTo("Driss");
        assertThat(user.getEmail()).isEqualTo("driss@cih.com");
        assertThat(user.getPassword()).isEqualTo("456");
    }
}
