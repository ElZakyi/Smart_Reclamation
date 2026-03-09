package com.cihbank.backend.rolepermission;

import com.cihbank.backend.permission.Permission;
import com.cihbank.backend.permission.PermissionService;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleService;
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
public class RolePermissionServiceTest {
    @Autowired
    RolePermissionService rolePermissionService;
    @Autowired
    RoleService roleService;
    @Autowired
    PermissionService permissionService;
    @Test
    void shouldAssignPermissionToRole(){
        Permission permission = new Permission();
        permission.setName("CREATE_RECLAMATION");
        Permission savedPermission = permissionService.createPermission(permission);
        Role role = new Role();
        role.setName("ADMIN");
        Role savedRole = roleService.createRole(role);
        rolePermissionService.assignPermissionToRole(savedRole.getIdRole(),savedPermission.getIdPermission());
        List<String> permissionList = rolePermissionService.getPermissionsByRole(role.getIdRole());
        assertThat(permissionList).contains("CREATE_RECLAMATION");
    }
    @Test
    void shouldReturnAllPermissionsByRole(){
        Permission permission = new Permission();
        permission.setName("CREATE_RECLAMATION");
        Permission permission1 = new Permission();
        permission1.setName("DELETE_RECLAMATION");
        Permission savedPermission1 = permissionService.createPermission(permission);
        Permission savedPermission2 = permissionService.createPermission(permission1);
        Role role = new Role();
        role.setName("ADMIN");
        Role savedRole = roleService.createRole(role);
        rolePermissionService.assignPermissionToRole(role.getIdRole(),savedPermission1.getIdPermission());
        rolePermissionService.assignPermissionToRole(role.getIdRole(),savedPermission2.getIdPermission());
        List<String> permissionList = rolePermissionService.getPermissionsByRole(role.getIdRole());
        assertThat(permissionList).hasSize(2);
    }
    @Test
    void shouldRemovePermissionFromUser(){
        Permission permission = new Permission();
        permission.setName("CREATE_RECLAMATION");
        Permission permissionSaved = permissionService.createPermission(permission);
        Role role = new Role();
        role.setName("ADMIN");
        Role roleSaved = roleService.createRole(role);
        rolePermissionService.assignPermissionToRole(roleSaved.getIdRole(),permissionSaved.getIdPermission());
        assertThat(rolePermissionService.getPermissionsByRole(roleSaved.getIdRole())).isNotEmpty();
        rolePermissionService.removePermissionFromRole(roleSaved.getIdRole(),permissionSaved.getIdPermission());
        assertThat(rolePermissionService.getPermissionsByRole(roleSaved.getIdRole())).isEmpty();
    }
}
