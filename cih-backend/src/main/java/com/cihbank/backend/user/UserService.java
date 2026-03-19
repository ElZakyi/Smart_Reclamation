package com.cihbank.backend.user;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import com.cihbank.backend.security.CustomUserDetails;
import com.cihbank.backend.userrole.UserRole;
import com.cihbank.backend.userrole.UserRoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    public UserService(UserRepository userRepository,AuditLogService auditLogService,RoleRepository roleRepository , UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User createUser(User user){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setIsActive(true);
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        auditLogService.log(AuditAction.CREATE_USER,"Utilisateur",saved.getIdUser(),currentUserId,null);
        return saved ;
    }
    @Transactional
    public void desactivate(Integer userId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable !"));
        user.setIsActive(false);
        User saved = userRepository.save(user);
        auditLogService.log(AuditAction.DEACTIVATE_USER,"Utilisateur",saved.getIdUser(),currentUserId,null);
    }
    @Transactional
    public void activate(Integer userId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable ! "));
        user.setIsActive(true);
        User saved = userRepository.save(user);
        auditLogService.log(AuditAction.ACTIVATE_USER,"Utilisateur",saved.getIdUser(),currentUserId,null);
    }
    @Transactional
    public void updateUser(Integer id, User updatedUser){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        User user = userRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        user.setFullName(updatedUser.getFullName());
        user.setPassword(updatedUser.getPassword());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setPasswordHash(passwordEncoder.encode(updatedUser.getPassword()));
        User saved = userRepository.save(user);
        auditLogService.log(AuditAction.UPDATE_USER,"Utilisateur",saved.getIdUser(),currentUserId,null);
    }
    public User getCurrentUser(Authentication authentication){

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
    }
    public User registerClient(User user){

        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow();

        userRoleRepository.save(
                new UserRole(saved,clientRole,LocalDateTime.now())
        );

        return saved;
    }
}
