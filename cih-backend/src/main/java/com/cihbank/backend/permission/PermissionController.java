package com.cihbank.backend.permission;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Permission createPermission(@RequestBody Permission permission){
        return permissionService.createPermission(permission);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Permission> getAllPermissions(){
        return permissionService.getAllPermissions();
    }

}
