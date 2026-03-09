package com.cihbank.backend.role;

import com.cihbank.backend.rolepermission.RolePermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Role> getRoles(){
        return roleService.getAllRoles();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Role createRole(@RequestBody Role role){
        return roleService.createRole(role);
    }
}
