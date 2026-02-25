package com.cihbank.backend.config;

import com.cihbank.backend.permission.Permission;
import com.cihbank.backend.permission.PermissionRepository;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import com.cihbank.backend.rolepermission.RolePermission;
import com.cihbank.backend.rolepermission.RolePermissionId;
import com.cihbank.backend.rolepermission.RolePermissionRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import com.cihbank.backend.userrole.UserRole;
import com.cihbank.backend.userrole.UserRoleId;
import com.cihbank.backend.userrole.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            String[] roles = {
                    "ADMIN",
                    "AGENT",
                    "RESPONSABLE",
                    "AUDITEUR",
            };

            for (String roleName : roles) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role r = new Role();
                            r.setName(roleName);
                            return roleRepository.save(r);
                        });
            }

            // 2) PERMISSIONS (catalogue)
            String[] permissions = {
                    // IAM
                    "CREATE_USER", "UPDATE_USER", "DEACTIVATE_USER","ACTIVATE_USER",
                    "ASSIGN_ROLE", "MANAGE_ROLE_PERMISSION", "ASSIGN_TEAM",

                    // RECLAMATION
                    "CREATE_RECLAMATION", "VIEW_RECLAMATION",
                    "UPDATE_RECLAMATION", "REOPEN_RECLAMATION",

                    // MESSAGE
                    "SEND_MESSAGE", "REQUEST_INFO", "PROVIDE_INFO",

                    // WORKFLOW
                    "START_TREATMENT", "VALIDATE_TRANSITION", "FORCE_TRANSITION",

                    // ROUTING
                    "ACCEPT_ROUTING", "MANUAL_REASSIGN",

                    // DECISION
                    "PROPOSE_DECISION", "VALIDATE_DECISION", "CLOSE_RECLAMATION",

                    // CARD
                    "VIEW_CARD", "BLOCK_CARD", "UPDATE_CARD_LIMIT",

                    // PLAFOND
                    "CREATE_PLAFOND_REQUEST", "GENERATE_OTP",
                    "VALIDATE_OTP", "SIMULATE_PLAFOND",
                    "VALIDATE_PLAFOND_CHANGE", "APPLY_PLAFOND",

                    // AUDIT
                    "VIEW_AUDIT_LOGS"
            };

            for (String permName : permissions) {
                permissionRepository.findByName(permName)
                        .orElseGet(() -> {
                            Permission p = new Permission();
                            p.setName(permName); // tu utilises findByName()
                            return permissionRepository.save(p);
                        });
            }
            // =========================
            // 3️⃣ ADMIN reçoit UNIQUEMENT permissions IAM
            // =========================

            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

            String[] adminPermissions = {
                    "CREATE_USER",
                    "UPDATE_USER",
                    "DEACTIVATE_USER",
                    "ACTIVATE_USER",
                    "ASSIGN_ROLE",
                    "MANAGE_ROLE_PERMISSION"
            };

            for (String permName : adminPermissions) {

                Permission permission =
                        permissionRepository.findByName(permName).orElseThrow();

                RolePermissionId id =
                        new RolePermissionId(
                                adminRole.getIdRole(),
                                permission.getIdPermission()
                        );

                if (!rolePermissionRepository.existsById(id)) {
                    rolePermissionRepository.save(
                            new RolePermission(adminRole, permission)
                    );
                }
            }

            // =========================
            // 4️⃣ CREATE ADMIN USER
            // =========================

            User adminUser = userRepository.findByEmail("admin@cih.com")
                    .orElseGet(() -> {

                        User u = new User();
                        u.setFullName("Admin");
                        u.setEmail("admin@cih.com");
                        u.setPhone("0600000000");
                        u.setPassword("1234");
                        u.setPasswordHash(passwordEncoder.encode(u.getPassword()));
                        u.setIsActive(true);
                        u.setCreatedAt(LocalDateTime.now());

                        return userRepository.save(u);
                    });

            // =========================
            // 5️⃣ ASSIGN ROLE ADMIN
            // =========================

            UserRoleId userRoleId =
                    new UserRoleId(
                            adminUser.getIdUser(),
                            adminRole.getIdRole()
                    );

            if (!userRoleRepository.existsById(userRoleId)) {
                userRoleRepository.save(
                        new UserRole(adminUser, adminRole, LocalDateTime.now())
                );
            }

            System.out.println("✅ BD initialisé correctement");
        };
    }
}

