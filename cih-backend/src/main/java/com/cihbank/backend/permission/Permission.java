package com.cihbank.backend.permission;

import com.cihbank.backend.rolepermission.RolePermission;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="permission")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_permission")
    private Integer idPermission;
    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany(mappedBy="permission")
    private Set<RolePermission> rolePermissions;

    public Permission(){}
    public Integer getIdPermission(){
        return idPermission;
    }
    public void setIdPermission(Integer idPermission){
        this.idPermission = idPermission;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

}
