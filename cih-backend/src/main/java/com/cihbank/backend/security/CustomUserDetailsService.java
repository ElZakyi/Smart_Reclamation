package com.cihbank.backend.security;

import com.cihbank.backend.rolepermission.RolePermission;
import com.cihbank.backend.rolepermission.RolePermissionRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import com.cihbank.backend.userrole.UserRole;
import com.cihbank.backend.userrole.UserRoleRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    UserRoleRepository userRoleRepository,
                                    RolePermissionRepository rolePermissionRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // ✅ On évite user.getUserRoles() (Lazy) → on charge via repository
        List<UserRole> userRoles = userRoleRepository.findByUserIdUser(user.getIdUser());

        // 1) roles (String)
        List<String> roles = userRoles.stream()
                .map(ur -> ur.getRole().getName())
                .distinct()
                .toList();

        // 2) permissions (String) via repository rolePermissionRepository
        List<String> permissions = roles.isEmpty()
                ? List.of()
                : userRoles.stream()
                .flatMap(ur -> rolePermissionRepository.findByRoleIdRole(ur.getRole().getIdRole()).stream())
                .map(rp -> rp.getPermission().getName())
                .distinct()
                .toList();

        // 3) authorities (ROLE_... + permissions)
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
        permissions.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));

        return new CustomUserDetails(
                user.getEmail(),
                user.getPasswordHash(), // ⚠️ important: hash stocké en DB
                roles,
                permissions,
                authorities
        );
    }
}
