package com.cihbank.backend.rolepermission;

import com.cihbank.backend.permission.Permission;
import com.cihbank.backend.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "role_permission")
public class RolePermission {
    @EmbeddedId
    private RolePermissionId id;
    @ManyToOne
    @MapsId("idRole")
    @JoinColumn(name = "id_role")
    @JsonIgnore
    private Role role;

    @ManyToOne
    @MapsId("idPermission")
    @JoinColumn(name = "id_permission")
    private Permission permission;

    public RolePermission() {
    }

    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
        this.id = new RolePermissionId(role.getIdRole(), permission.getIdPermission());
    }

    public RolePermissionId getId() {
        return id;
    }

    public void setId(RolePermissionId id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

}
