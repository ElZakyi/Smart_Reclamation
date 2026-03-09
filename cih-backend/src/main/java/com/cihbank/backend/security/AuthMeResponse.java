package com.cihbank.backend.security;


import java.util.List;

public class AuthMeResponse {
    private Integer idUser;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    public AuthMeResponse(Integer idUser,String email, List<String> roles, List<String> permissions){
        this.idUser = idUser;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }
    public Integer getIdUser(){return idUser;}
    public String getEmail(){
        return email;
    }
    public List<String> getRoles(){
        return roles;
    }
    public List<String> getPermissions(){
        return permissions;
    }

}
