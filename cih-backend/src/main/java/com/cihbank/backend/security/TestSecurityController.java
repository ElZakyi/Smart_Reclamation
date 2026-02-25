package com.cihbank.backend.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestSecurityController {

    @PreAuthorize("hasAuthority('CREATE_RECLAMATION')")
    @GetMapping("/secure/test")
    public String secureEndPoint(Authentication authentication){
        return "Acces granted !";
    }
}
