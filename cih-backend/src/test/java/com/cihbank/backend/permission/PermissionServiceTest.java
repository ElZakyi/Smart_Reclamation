package com.cihbank.backend.permission;

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
public class PermissionServiceTest {
    @Autowired
    private PermissionService permissionService;
    @Test
    void shouldCreatePermission(){
        Permission permission = new Permission();
        permission.setName("CREATE_RECLAMATION");
        Permission permissionSaved = permissionService.createPermission(permission);
        assertThat(permissionSaved.getIdPermission()).isNotNull();
    }
    @Test
    void shouldReturnAllPermissions(){
        Permission permission1 = new Permission();
        permission1.setName("CREATE_RECLAMATION");
        Permission permission2 = new Permission();
        permission2.setName("DELETE_RECLAMATION");
        Permission savedPermission1 = permissionService.createPermission(permission1);
        Permission savedPermission2 = permissionService.createPermission(permission2);
        List<Permission> permissionList = permissionService.getAllPermissions();
        assertThat(permissionList).hasSize(2);
    }
}
