package com.cihbank.backend.rolepermission;

import com.cihbank.backend.permission.Permission;
import com.cihbank.backend.permission.PermissionRepository;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

@DataJpaTest
public class RolePermissionRepositoryTest {
    @Autowired
    private RolePermissionRepository rolePermissionRepository ;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Test
    void shouldFindPermissionByRole(){
        Permission permission = new Permission();
        permission.setName("CREATE_RECLAMATION");
        Permission permissionSaved = permissionRepository.save(permission);
        Role role = new Role();
        role.setName("ADMIN");
        Role roleSaved = roleRepository.save(role);
        RolePermission rolePermission = new RolePermission(roleSaved,permissionSaved);
        rolePermissionRepository.save(rolePermission);
        List<RolePermission> rolePermissionList = rolePermissionRepository.findByRoleIdRole(role.getIdRole());
        Assertions.assertThat(rolePermissionList).hasSize(1);

    }
}
