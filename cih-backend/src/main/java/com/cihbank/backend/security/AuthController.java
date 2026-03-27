package com.cihbank.backend.security;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        try {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

            if (!user.getIsActive()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Compte désactivé !"));
            }

            String token = jwtService.generateToken(request.getEmail());

            return ResponseEntity.ok(token);

        } catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou mot de passe incorrect"));
        }
    }
    @GetMapping("/me")
    public AuthMeResponse me(Authentication authentication){

        if(authentication == null){
            throw new RuntimeException("Utilisateur non authentifié !");
        }

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        assert user != null;
        return new AuthMeResponse(
                user.getIdUser(),
                user.getUsername(),
                user.getRoles(),
                user.getPermissions()
        );
    }

}
