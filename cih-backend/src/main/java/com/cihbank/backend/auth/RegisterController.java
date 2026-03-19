package com.cihbank.backend.auth;

import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService){
        this.userService = userService;
    }

    // 🔓 PUBLIC
    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.registerClient(user);
    }
}