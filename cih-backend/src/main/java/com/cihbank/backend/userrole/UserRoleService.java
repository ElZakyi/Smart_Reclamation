package com.cihbank.backend.userrole;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import com.cihbank.backend.security.CustomUserDetails;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditLogService auditLogService;
    public UserRoleService(UserRoleRepository userRoleRepository,AuditLogService auditLogService,UserRepository userRepository,RoleRepository roleRepository){
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditLogService = auditLogService;
    }
    //assign role to user , error in 1 opperation equal to rollBack (nothing happen) !
    @Transactional
    public void assignRoleToUser(Integer userId, Integer roleId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId).orElseThrow(()-> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Utilisateur introuvable !"
        ));
        // Vérifier que le rôle existe
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Rôle introuvable !"
        ));
        // Vérifier si la relation existe déjà
        UserRoleId userRoleId = new UserRoleId(user.getIdUser(),role.getIdRole());
        if(userRoleRepository.existsById(userRoleId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'utilisateur possède déjà ce rôle !"
            );
        }
        UserRole userRole = new UserRole(user,role,LocalDateTime.now());
        UserRole saved = userRoleRepository.save(userRole);
        auditLogService.log(AuditAction.ASSIGN_ROLE,"Utilisateur",user.getIdUser(),currentUserId,null);
    }
    // get the role of a user and list only the name (we use stream cause it gives the advantage to use map)
    public List<String> getRolesByUser(Integer userId){
        List<UserRole> userRoles = userRoleRepository.findByUserIdUser(userId);
        return userRoles.stream().map(userRole -> userRole.getRole().getName()).collect(Collectors.toList());
    }
    // remove role from user (simply remove UserRole) from database
    @Transactional
    public void removeRoleFromUser(Integer idUser, Integer idRole){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        UserRoleId id = new UserRoleId(idUser, idRole);
        if(!userRoleRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"UserRole introuvable !");
        userRoleRepository.deleteById(id);
        auditLogService.log(AuditAction.REMOVE_ROLE,"Utilisateur",idUser,currentUserId,null);
    }
    @Transactional
    public void updateUserRole(Integer userId, Integer newRoleId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = userDetails.getIdUser();
        User user = userRepository.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Utilisateur introuvable !"));
        Role role = roleRepository.findById(newRoleId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Rôle introuvable  !"));
        userRoleRepository.deleteByUser_IdUser(userId);
        UserRole userRole = new UserRole(user,role,LocalDateTime.now());
        userRoleRepository.save(userRole);
        auditLogService.log(AuditAction.UPDATE_ROLE,"Utilisateur",userId,currentUserId,null);
    }
}
