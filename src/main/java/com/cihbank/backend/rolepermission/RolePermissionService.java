package com.cihbank.backend.rolepermission;

import com.cihbank.backend.permission.Permission;
import com.cihbank.backend.permission.PermissionRepository;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolePermissionService {
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RolePermissionService(RolePermissionRepository rolePermissionRepository,RoleRepository roleRepository,PermissionRepository permissionRepository){
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    // assign permission to a role
    @Transactional
    public void assignPermissionToRole(Integer roleId, Integer permissionId){
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Role not found !"));
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Permission not found !"));
        RolePermissionId rolePermissionId = new RolePermissionId(roleId,permissionId);
        if(rolePermissionRepository.existsById(rolePermissionId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Permission already assigned to role !");
        RolePermission rolePermission = new RolePermission(role,permission);
        rolePermissionRepository.save(rolePermission);
    }
    // get all permission assigned to a role
    public List<String> getPermissionsByRole(Integer idRole){
        List<RolePermission> rolePermissions =  rolePermissionRepository.findByRoleIdRole(idRole);
        return rolePermissions.stream().map(rolePermission -> rolePermission.getPermission().getName()).collect(Collectors.toList());
    }
    // remove permission assigned to a role
    @Transactional
    public void removePermissionFromRole(Integer idRole, Integer idPermission){
        RolePermissionId rolePermissionId = new RolePermissionId(idRole,idPermission);
        if(! rolePermissionRepository.existsById(rolePermissionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Permission not assigned to role !");
        }
        rolePermissionRepository.deleteById(rolePermissionId);
    }
}
