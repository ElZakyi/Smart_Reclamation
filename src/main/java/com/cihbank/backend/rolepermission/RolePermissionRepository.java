package com.cihbank.backend.rolepermission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission,RolePermissionId> {
    List<RolePermission> findByRoleIdRole(Integer idRole);
}
