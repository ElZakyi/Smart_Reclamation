package com.cihbank.backend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<User> getUsers(){
        return userService.getAllUsers();
    }
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @PostMapping
    public User createUser(@RequestBody User user){
        return userService.createUser(user);
    }
    @PreAuthorize("hasAuthority('DEACTIVATE_USER')")
    @PatchMapping("{id}/desactivate")
    public String desactivate(@PathVariable Integer id){
        userService.desactivate(id);
        return "User désactivé !";
    }
    @PreAuthorize("hasAuthority('ACTIVATE_USER')")
    @PatchMapping("{id}/activate")
    public String activate(@PathVariable Integer id){
        userService.activate(id);
        return "User activé !";
    }
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Integer id, @RequestBody User userUpdate){
        userService.updateUser(id,userUpdate);
        return "User has been updated successfully";
    }
}
