package com.cihbank.backend.config;

import com.cihbank.backend.permission.Permission;
import com.cihbank.backend.permission.PermissionRepository;
import com.cihbank.backend.reclamation.enums.ReclamationStatus;
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
import com.cihbank.backend.workflowtransition.WorkflowTransition;
import com.cihbank.backend.workflowtransition.WorkflowTransitionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Profile("!test")
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            WorkflowTransitionRepository workflowRepository
    ) {
        return args -> {

            // =========================
            // 1️⃣ ROLES
            // =========================
            String[] roles = {
                    "ADMIN", "AGENT", "RESPONSABLE", "AUDITEUR", "CLIENT"
            };

            for (String roleName : roles) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role r = new Role();
                            r.setName(roleName);
                            return roleRepository.save(r);
                        });
            }

            Role agent = roleRepository.findByName("AGENT").orElseThrow();
            Role responsable = roleRepository.findByName("RESPONSABLE").orElseThrow();

            // =========================
            // 2️⃣ WORKFLOW TRANSITIONS (ANTI-DOUBLON)
            // =========================
            createTransitionIfNotExists(workflowRepository,
                    ReclamationStatus.RESOLUE,
                    ReclamationStatus.EN_VALIDATION,
                    agent);

            createTransitionIfNotExists(workflowRepository,
                    ReclamationStatus.EN_VALIDATION,
                    ReclamationStatus.CLOTUREE,
                    responsable);

            createTransitionIfNotExists(workflowRepository,
                    ReclamationStatus.EN_VALIDATION,
                    ReclamationStatus.AFFECTEE,
                    responsable);

            createTransitionIfNotExists(workflowRepository,
                    ReclamationStatus.AFFECTEE,
                    ReclamationStatus.RESOLUE,
                    agent);

            // =========================
            // 3️⃣ PERMISSIONS
            // =========================
            String[] permissions = {
                    "CREATE_USER", "UPDATE_USER", "DEACTIVATE_USER","ACTIVATE_USER",
                    "ASSIGN_ROLE", "MANAGE_ROLE_PERMISSION",

                    "CREATE_RECLAMATION", "VIEW_RECLAMATION", "DELETE_RECLAMATION",
                    "UPDATE_RECLAMATION", "REOPEN_RECLAMATION",

                    "SEND_MESSAGE", "REQUEST_INFO", "PROVIDE_INFO",

                    "START_TREATMENT", "VALIDATE_TRANSITION", "FORCE_TRANSITION",

                    "ACCEPT_ROUTING", "MANUAL_REASSIGN",

                    "PROPOSE_DECISION", "VALIDATE_DECISION", "CLOSE_RECLAMATION",

                    "VIEW_CARD", "BLOCK_CARD", "UPDATE_CARD_LIMIT",

                    "CREATE_PLAFOND_REQUEST", "GENERATE_OTP",
                    "VALIDATE_OTP", "SIMULATE_PLAFOND",
                    "VALIDATE_PLAFOND_CHANGE", "APPLY_PLAFOND",

                    "VIEW_ATTACHMENT" , "UPLOAD_ATTACHMENT", "DELETE_ATTACHMENT",

                    "VIEW_AUDIT_LOGS",

                    "CREATE_TEAM","UPDATE_TEAM","ACTIVATE_TEAM","DEACTIVATE_TEAM",
                    "VIEW_TEAM","ASSIGN_TEAM","REMOVE_TEAM_MEMBER"
            };

            for (String permName : permissions) {
                permissionRepository.findByName(permName)
                        .orElseGet(() -> {
                            Permission p = new Permission();
                            p.setName(permName);
                            return permissionRepository.save(p);
                        });
            }

            // =========================
            // 4️⃣ ADMIN PERMISSIONS
            // =========================
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();

            String[] adminPermissions = {
                    "CREATE_USER", "UPDATE_USER", "DEACTIVATE_USER","ACTIVATE_USER",
                    "ASSIGN_ROLE", "MANAGE_ROLE_PERMISSION",
                    "CREATE_TEAM","UPDATE_TEAM","ACTIVATE_TEAM","DEACTIVATE_TEAM",
                    "VIEW_TEAM","ASSIGN_TEAM","REMOVE_TEAM_MEMBER"
            };

            for (String permName : adminPermissions) {
                Permission permission = permissionRepository.findByName(permName).orElseThrow();

                RolePermissionId id = new RolePermissionId(
                        adminRole.getIdRole(),
                        permission.getIdPermission()
                );

                if (!rolePermissionRepository.existsById(id)) {
                    rolePermissionRepository.save(new RolePermission(adminRole, permission));
                }
            }

            // =========================
            // 5️⃣ ADMIN USER
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
            // 6️⃣ ASSIGN ADMIN ROLE
            // =========================
            UserRoleId userRoleId = new UserRoleId(
                    adminUser.getIdUser(),
                    adminRole.getIdRole()
            );

            if (!userRoleRepository.existsById(userRoleId)) {
                userRoleRepository.save(
                        new UserRole(adminUser, adminRole, LocalDateTime.now())
                );
            }

            System.out.println("✅ BD initialisée correctement (sans doublons)");
        };
    }

    // =========================
    // 🔥 MÉTHODE ANTI-DOUBLON
    // =========================
    private void createTransitionIfNotExists(
            WorkflowTransitionRepository repo,
            ReclamationStatus from,
            ReclamationStatus to,
            Role role
    ) {

        boolean exists = repo
                .findByFromStatusAndToStatusAndRole_NameAndIsActiveTrue(
                        from, to, role.getName()
                )
                .isPresent();

        if (!exists) {
            WorkflowTransition t = new WorkflowTransition();
            t.setFromStatus(from);
            t.setToStatus(to);
            t.setRole(role);
            t.setActive(true);
            t.setCreatedAt(LocalDateTime.now());

            repo.save(t);
        }
    }
}