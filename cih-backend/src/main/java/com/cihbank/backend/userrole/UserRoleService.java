package com.cihbank.backend.userrole;

import com.cihbank.backend.role.Role;
import com.cihbank.backend.role.RoleRepository;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    public UserRoleService(UserRoleRepository userRoleRepository,UserRepository userRepository,RoleRepository roleRepository){
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    //assign role to user , error in 1 opperation equal to rollBack (nothing happen) !
    @Transactional
    public void assignRoleToUser(Integer userId, Integer roleId){
        // Vérifier que l'utilisateur existe
        User user = userRepository.findById(userId).orElseThrow(()-> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
        ));
        // Vérifier que le rôle existe
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Role not found"
        ));
        // Vérifier si la relation existe déjà
        UserRoleId userRoleId = new UserRoleId(user.getIdUser(),role.getIdRole());
        if(userRoleRepository.existsById(userRoleId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User already has this role"
            );
        }
        UserRole userRole = new UserRole(user,role,LocalDateTime.now());
        userRoleRepository.save(userRole);

    }
    // get the role of a user and list only the name (we use stream cause it gives the advantage to use map)
    public List<String> getRolesByUser(Integer userId){
        List<UserRole> userRoles = userRoleRepository.findByUserIdUser(userId);
        return userRoles.stream().map(userRole -> userRole.getRole().getName()).collect(Collectors.toList());
    }
    // remove role from user (simply remove UserRole) from database
    @Transactional
    public void removeRoleFromUser(Integer idUser, Integer idRole){
        UserRoleId id = new UserRoleId(idUser, idRole);
        if(!userRoleRepository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"UserRole not found !");
        userRoleRepository.deleteById(id);
    }
    @Transactional
    public void updateUserRole(Integer userId, Integer newRoleId){
        User user = userRepository.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found !"));
        Role role = roleRepository.findById(newRoleId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Role not found !"));
        userRoleRepository.deleteByUser_IdUser(userId);
        UserRole userRole = new UserRole(user,role,LocalDateTime.now());
        userRoleRepository.save(userRole);
    }
}
