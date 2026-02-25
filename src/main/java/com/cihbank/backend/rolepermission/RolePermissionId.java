package com.cihbank.backend.rolepermission;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class RolePermissionId {
    @Column(name="id_role")
    private Integer idRole;
    @Column(name="id_permission")
    private Integer idPermission;
    public RolePermissionId(){}
    public RolePermissionId(Integer idRole, Integer idPermission){
        this.idRole = idRole;
        this.idPermission = idPermission;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolePermissionId)) return false;
        RolePermissionId that = (RolePermissionId) o;
        return Objects.equals(idRole, that.idRole) &&
                Objects.equals(idPermission, that.idPermission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRole, idPermission);
    }
}
