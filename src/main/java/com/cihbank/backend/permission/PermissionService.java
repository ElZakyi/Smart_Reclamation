package com.cihbank.backend.permission;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    public PermissionService(PermissionRepository permissionRepository){
        this.permissionRepository = permissionRepository;
    }
    public Permission createPermission(Permission permission){
        return permissionRepository.save(permission);
    }
    public List<Permission> getAllPermissions(){
        return permissionRepository.findAll();
    }
}
