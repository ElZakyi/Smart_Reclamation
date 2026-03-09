package com.cihbank.backend.userrole;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserRoleId implements Serializable {
    @Column(name="id_user")
    private Integer idUser;
    @Column(name="id_role")
    private Integer idRole;
    public UserRoleId(){}
    public UserRoleId(Integer idUser, Integer idRole) {
        this.idUser = idUser;
        this.idRole = idRole;
    }
    public Integer getidUser(){
        return idUser;
    }
    public void setidUser(Integer idUser){
        this.idUser = idUser;
    }
    public Integer getidRole(){
        return idRole;
    }
    public void setidRole(Integer idRole){
        this.idRole = idRole;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleId)) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(idUser, that.idUser) &&
                Objects.equals(idRole, that.idRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, idRole);
    }


}
