package com.cihbank.backend.role;

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
public class RoleServiceTest {
    @Autowired
    RoleService roleService;
    @Test
    void shouldCreateRole(){
        Role role = new Role();
        role.setName("CREATE_RECLAMATION");
        Role savedRole = roleService.createRole(role);
        assertThat(savedRole.getIdRole()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("CREATE_RECLAMATION");

    }
    @Test
    void shouldReturnAllRoles(){
        Role role1 = new Role();
        role1.setName("CREATE_RECLAMATION");
        Role role2 = new Role();
        role2.setName("DELETE_RECLAMATION");
        Role savedRole1 = roleService.createRole(role1);
        Role savedRole2 = roleService.createRole(role2);
        List<Role> roleList = roleService.getAllRoles();
        assertThat(roleList).hasSize(2);
    }
}
