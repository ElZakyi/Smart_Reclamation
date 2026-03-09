package com.cihbank.backend.rolepermission;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-permissions")
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;
    public RolePermissionController(RolePermissionService rolePermissionService){
        this.rolePermissionService = rolePermissionService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign/roles/{idRole}/permissions/{idPermission}")
    public String assignPermissionToRole(@PathVariable Integer idRole, @PathVariable Integer idPermission){
        rolePermissionService.assignPermissionToRole(idRole, idPermission);
        return "Permission assigned to role successfully";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{idRole}")
    public List<String> getPermissionByRole(@PathVariable Integer idRole){
        return rolePermissionService.getPermissionsByRole(idRole);
    }
    @PreAuthorize("hasAuthority('MANAGE_ROLE_PERMISSION')")
    @DeleteMapping("/remove/roles/{idRole}/permissions/{idPermission}")
    public String removePermissionFromRole(@PathVariable Integer idRole, @PathVariable Integer idPermission){
        rolePermissionService.removePermissionFromRole(idRole,idPermission);
        return "Permission removed from role successfully";
    }
}
