package com.cihbank.backend.security;

import com.cihbank.backend.permission.Permission;
import com.cihbank.backend.permission.PermissionRepository;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import com.cihbank.backend.rolepermission.RolePermission;
import com.cihbank.backend.rolepermission.RolePermissionRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import com.cihbank.backend.userrole.UserRole;
import com.cihbank.backend.userrole.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityAccessTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private RolePermissionRepository rolePermissionRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        userRoleRepository.deleteAll();
        rolePermissionRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();


        Permission permission = new Permission();
        permission.setName("CREATE_RECLAMATION");
        permissionRepository.save(permission);

        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        RolePermission rolePermission = new RolePermission(role,permission);
        rolePermissionRepository.save(rolePermission);

        User user = new User();
        user.setFullName("Admin");
        user.setEmail("admin@cih.com");
        user.setPasswordHash(passwordEncoder.encode("1234"));
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setIsAccountExpired(false);
        user.setIsCredentialExpired(false);
        user.setIsAccountLocked(false);
        userRepository.save(user);

        UserRole userRole = new UserRole(user,role,user.getCreatedAt());
        userRoleRepository.save(userRole);
    }
    @Test
    void secureEndPointShouldReturn403WithoutToken() throws Exception{
        mockMvc.perform(get("/api/secure/test")).andExpect(status().isForbidden());
    }
    @Test
    void secureEndPointShouldReturn200WithValidToken() throws Exception{
        String loginRequest = """
                {
                    "email": "admin@cih.com",
                    "password": "1234"
                }
                """;
        String token = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(loginRequest))
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(get("/api/secure/test").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(content().string("Acces granted !"));
    }
}
