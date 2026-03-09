package com.cihbank.backend.role;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;
    @Test
    public void shouldCreateRole(){
        Role role = new Role();
        role.setName("ADMIN");
        Role savedRole = roleRepository.save(role);
        assertThat(savedRole.getIdRole()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("ADMIN");

    }
}
