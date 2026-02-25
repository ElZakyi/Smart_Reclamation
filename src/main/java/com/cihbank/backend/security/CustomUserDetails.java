package com.cihbank.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private String email;
    private String password;

    private List<String> roles;
    private List<String> permissions;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(
            String email,
            String password,
            List<String> roles,
            List<String> permissions,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.permissions = permissions;
        this.authorities = authorities;
    }

    // ====== UserDetails obligatoire ======

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // ====== NOS méthodes custom ======

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
