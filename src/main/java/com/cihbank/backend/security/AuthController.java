package com.cihbank.backend.security;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request){
        try {
            authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            String token = jwtService.generateToken(request.getEmail());

            return token;
        }catch(Exception e){
            e.printStackTrace();
            throw e;

        }
    }
    @GetMapping("/me")
    public AuthMeResponse me(Authentication authentication){

        if(authentication == null){
            throw new RuntimeException("User not authenticated");
        }

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        assert user != null;
        return new AuthMeResponse(
                user.getUsername(),
                user.getRoles(),
                user.getPermissions()
        );
    }

}
