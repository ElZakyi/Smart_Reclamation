package com.cihbank.backend.userrole;

import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRoleRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    void shouldAssignRoleToUser(){
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@mail.com");
        user.setPasswordHash("123");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        Role role = new Role();
        role.setName("ADMIN");
        role = roleRepository.save(role);

        UserRoleId id = new UserRoleId(user.getIdUser(),role.getIdRole());
        UserRole userRole = new UserRole(user,role,LocalDateTime.now());
        userRole.setId(id);
        userRole.setAssignedAt(LocalDateTime.now());

        userRoleRepository.save(userRole);
        List<UserRole> roles = userRoleRepository.findByUserIdUser(user.getIdUser());
        assertThat(roles).hasSize(1);
    }
    @Test
    void shouldDeleteRoleFromUser(){
        User user = new User();
        user.setFullName("Test User");
        user.setEmail("test@mail.com");
        user.setPasswordHash("123");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        Role role = new Role();
        role.setName("ADMIN");
        Role savedRole = roleRepository.save(role);

        UserRole userRole = new UserRole(savedUser,savedRole,LocalDateTime.now());
        UserRole savedUserRole = userRoleRepository.save(userRole);
        UserRoleId userRoleId = new UserRoleId(user.getIdUser(),role.getIdRole());
        assertThat(userRoleRepository.existsById(userRoleId)).isTrue();
        userRoleRepository.deleteById(userRoleId);
        assertThat(userRoleRepository.existsById(userRoleId)).isFalse();

    }
}
