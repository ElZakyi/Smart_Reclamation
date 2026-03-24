package com.cihbank.backend.otp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
public class OTPController {

    private final OTPService otpService;

    public OTPController(OTPService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public String generate(
            @RequestParam Integer userId,
            @RequestParam Integer cardId,
            @RequestParam Double limit,
            @RequestParam String justification
    ) {
        otpService.generateOtp(userId, cardId, limit, justification);
        return "OTP envoyé par SMS";
    }

    @PostMapping("/verify")
    public String verify(
            @RequestParam Integer userId,
            @RequestParam String code
    ) {
        otpService.verifyOtp(userId, code);
        return "Demande confirmée";
    }
}