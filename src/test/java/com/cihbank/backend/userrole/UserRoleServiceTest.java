package com.cihbank.backend.userrole;

import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleService;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRoleServiceTest {
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Test
    void shouldAssignRoleToUser(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zak@mail.com");
        user.setPasswordHash("123");
        Role role = new Role();
        role.setName("ADMIN");
        User savedUser = userService.createUser(user);
        Role savedRole = roleService.createRole(role);
        userRoleService.assignRoleToUser(savedUser.getIdUser(),savedRole.getIdRole());
        assertThat(userRoleService.getRolesByUser(savedUser.getIdUser())).contains("ADMIN");
    }
    @Test
    void shouldReturnAllRolesByUser(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setPasswordHash("123");
        user.setEmail("zak@mail.com");
        User userSaved = userService.createUser(user);
        Role role = new Role();
        role.setName("ADMIN");
        Role roleSaved = roleService.createRole(role);
        Role role1 = new Role();
        role1.setName("RESP");
        Role roleSaved1 = roleService.createRole(role1);
        userRoleService.assignRoleToUser(userSaved.getIdUser(),roleSaved.getIdRole());
        userRoleService.assignRoleToUser(userSaved.getIdUser(),roleSaved1.getIdRole());
        List<String> roleList = userRoleService.getRolesByUser(userSaved.getIdUser());
        assertThat(roleList).hasSize(2);
    }
    @Test
    void shouldRemoveRoleFromUser(){
        User user = new User();
        user.setFullName("Zakaria");
        user.setEmail("zak@email.com");
        user.setPasswordHash("123");
        User savedUser = userService.createUser(user);
        Role role = new Role();
        role.setName("ADMIN");
        Role savedRole = roleService.createRole(role);
        userRoleService.assignRoleToUser(savedUser.getIdUser(),savedRole.getIdRole());
        assertThat(userRoleService.getRolesByUser(savedUser.getIdUser())).isNotEmpty();
        userRoleService.removeRoleFromUser(savedUser.getIdUser(),savedRole.getIdRole());
        assertThat(userRoleService.getRolesByUser(savedUser.getIdUser())).isEmpty();
    }
    @Test
    void shouldUpdateUserRole(){
        User user = new User();
        user.setFullName("zakaria");
        user.setPassword("123");
        user.setEmail("zak@cih.com");
        User userSaved = userService.createUser(user);
        Role role = new Role();
        role.setName("ADMIN_TEST");
        Role roleSaved = roleService.createRole(role);
        UserRole userRole = new UserRole(userSaved,roleSaved,LocalDateTime.now());
        userRoleRepository.save(userRole);
        assertThat(userRole.getUser().getIdUser()).isEqualTo(userSaved.getIdUser());
        assertThat(userRole.getRole().getName()).isEqualTo("ADMIN_TEST");
        Role newRole = new Role();
        newRole.setName("AGENT_TEST");
        Role savedRoleUpdated = roleService.createRole(newRole);
        userRoleService.updateUserRole(userSaved.getIdUser(), savedRoleUpdated.getIdRole());
        UserRole updated = userRoleRepository.findByUserIdUser(userSaved.getIdUser()).get(0);
        assertThat(userRole.getUser().getIdUser()).isEqualTo(userSaved.getIdUser());
        assertThat(updated.getRole().getName()).isEqualTo("AGENT_TEST");
    }
}
