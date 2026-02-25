package com.cihbank.backend.role;

import com.cihbank.backend.rolepermission.RolePermission;
import com.cihbank.backend.userrole.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_role")
    private Integer idRole;
    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private Set<UserRole> userRoles;
    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private Set<RolePermission> rolePermissions;
    public Role(){}
    public Integer getIdRole(){
        return idRole;
    }
    public void setIdRole(Integer idRole){
        this.idRole = idRole;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public Set<UserRole> getUserRoles(){
        return userRoles;
    }
    public void setUserRoles(Set<UserRole> userRoles){
        this.userRoles = userRoles;
    }
    public Set<RolePermission> getRolePermissions() {return rolePermissions;}
    public void setRolePermissions(Set<RolePermission> rolePermissions){ this.rolePermissions = rolePermissions;}
}
