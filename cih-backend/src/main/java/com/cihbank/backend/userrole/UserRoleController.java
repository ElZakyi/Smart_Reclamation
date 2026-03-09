package com.cihbank.backend.userrole;

import com.cihbank.backend.role.Role;
import com.cihbank.backend.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {
    private final UserRoleService userRoleService;
    public UserRoleController(UserRoleService userRoleService){
        this.userRoleService = userRoleService;
    }
    //assign role to user
    @GetMapping("/whoami")
    public Object whoami(Authentication authentication) {
        if(authentication == null) {
            return "Authentication is NULL";
        }
        return authentication;
    }
    @PreAuthorize("hasAuthority('ASSIGN_ROLE')")
    @PostMapping("/assign/users/{userId}/roles/{roleId}")
    public String assignRole(@PathVariable Integer userId, @PathVariable Integer roleId,Authentication authentication){
        userRoleService.assignRoleToUser(userId,roleId);
        return "Role assigned succesfully";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("user/{idUser}")
    public List<String> getUserRoles(@PathVariable Integer idUser) {
        return userRoleService.getRolesByUser(idUser);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove/users/{userId}/roles/{roleId}")
    public String removeRoleFromUser(@PathVariable Integer userId,@PathVariable  Integer roleId){
        userRoleService.removeRoleFromUser(userId, roleId);
        return "Role removed from user successfully";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/users/{userId}/roles/{roleId}")
    public String updateUserRole(@PathVariable Integer userId,@PathVariable Integer roleId){
        userRoleService.updateUserRole(userId,roleId);
        return "Role updated successfully !" ;
    }
}
