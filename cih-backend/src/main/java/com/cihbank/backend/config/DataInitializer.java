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
import org.springframework.transaction.annotation.Transactional;

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
            Role agent = roleRepository.findByName("AGENT").get();
            Role responsable = roleRepository.findByName("RESPONSABLE").get();

            // AGENT → EN_VALIDATION
            WorkflowTransition t1 = new WorkflowTransition();
            t1.setFromStatus(ReclamationStatus.RESOLUE);
            t1.setToStatus(ReclamationStatus.EN_VALIDATION);
            t1.setRole(agent);
            t1.setActive(true);
            t1.setCreatedAt(LocalDateTime.now());

            // RESPONSABLE → CLOTURE
            WorkflowTransition t2 = new WorkflowTransition();
            t2.setFromStatus(ReclamationStatus.EN_VALIDATION);
            t2.setToStatus(ReclamationStatus.CLOTUREE);
            t2.setRole(responsable);
            t2.setActive(true);
            t2.setCreatedAt(LocalDateTime.now());

            // RESPONSABLE → RETOUR
            WorkflowTransition t3 = new WorkflowTransition();
            t3.setFromStatus(ReclamationStatus.EN_VALIDATION);
            t3.setToStatus(ReclamationStatus.AFFECTEE);
            t3.setRole(responsable);
            t3.setActive(true);
            t3.setCreatedAt(LocalDateTime.now());

            WorkflowTransition t4 = new WorkflowTransition();
            t4.setFromStatus(ReclamationStatus.AFFECTEE);
            t4.setToStatus(ReclamationStatus.RESOLUE);
            t4.setRole(agent);
            t4.setActive(true);
            t4.setCreatedAt(LocalDateTime.now());

            workflowRepository.saveAll(List.of(t1,t2,t3,t4));

            String[] roles = {
                    "ADMIN",
                    "AGENT",
                    "RESPONSABLE",
                    "AUDITEUR",
                    "CLIENT"
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
                    "ASSIGN_ROLE", "MANAGE_ROLE_PERMISSION",

                    // RECLAMATION
                    "CREATE_RECLAMATION", "VIEW_RECLAMATION", "DELETE_RECLAMATION",
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

                    // ATTACHMENT
                    "VIEW_ATTACHMENT" , "UPLOAD_ATTACHMENT", "DELETE_ATTACHMENT",

                    // AUDIT
                    "VIEW_AUDIT_LOGS",
                    //TEAM
                    "CREATE_TEAM","UPDATE_TEAM","ACTIVATE_TEAM","DEACTIVATE_TEAM","VIEW_TEAM"
                    ,"ASSIGN_TEAM","REMOVE_TEAM_MEMBER"
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
                    "MANAGE_ROLE_PERMISSION",
                    "CREATE_TEAM","UPDATE_TEAM","ACTIVATE_TEAM","DEACTIVATE_TEAM","VIEW_TEAM"
                    ,"ASSIGN_TEAM","REMOVE_TEAM_MEMBER"
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

