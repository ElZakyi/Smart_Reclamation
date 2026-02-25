package com.cihbank.backend.permission;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PermissionRepositoryTest {
    @Autowired
    private PermissionRepository permissionRepository;
    @Test
    void shouldCreatePermission(){
        Permission permission = new Permission();
        permission.setName("CREATE_RECLAMATION");

        Permission permissionSaved = permissionRepository.save(permission);
        assertThat(permissionSaved.getIdPermission()).isNotNull();
        assertThat(permissionSaved.getName()).isEqualTo("CREATE_RECLAMATION");
    }
    @Test
    void shouldFindPermissionByName(){
        Permission permission = new Permission();
        permission.setName("DELETE_USER");
        permissionRepository.save(permission);
        Optional<Permission> found = permissionRepository.findByName("DELETE_USER");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("DELETE_USER");
    }
}
