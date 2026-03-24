package com.cihbank.backend.otp;

import com.cihbank.backend.audit.AuditAction;
import com.cihbank.backend.audit.AuditLogService;
import com.cihbank.backend.plafondrequest.PlafondRequestService;
import com.cihbank.backend.user.User;
import com.cihbank.backend.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OTPService {

    private final OTPVerificationRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final SMSService smsService;
    private final UserRepository userRepository;
    private final PlafondRequestService plafondRequestService;
    private final AuditLogService auditLogService;

    public OTPService(
            OTPVerificationRepository otpRepository,
            PasswordEncoder passwordEncoder,
            SMSService smsService,
            UserRepository userRepository,
            PlafondRequestService plafondRequestService,
            AuditLogService auditLogService
    ) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.smsService = smsService;
        this.userRepository = userRepository;
        this.plafondRequestService = plafondRequestService;
        this.auditLogService = auditLogService;
    }

    // 🔥 GENERATE OTP + SMS INFOBIP
    public void generateOtp(Integer userId, Integer cardId, Double limit, String justification) {

        String code = String.valueOf(new Random().nextInt(9000) + 1000);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User introuvable"));

        OTPVerification otp = new OTPVerification();

        otp.setCodeHash(passwordEncoder.encode(code));
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        otp.setAttemptCount(0);
        otp.setMaxAttempts(3);
        otp.setStatus(OTPStatus.GENERATED);
        otp.setCreatedAt(LocalDateTime.now());

        otp.setTempUserId(userId);
        otp.setTempCardId(cardId);
        otp.setTempLimit(limit);
        otp.setTempJustification(justification);

        OTPVerification otpSaved = otpRepository.save(otp);

        auditLogService.log(AuditAction.GENERATE_OTP,"Otp",otpSaved.getIdOtp(),userId,null);
        // 🔥 SMS INFOBIP
        smsService.sendSms(
                user.getPhone(),
                "CIH Bank OTP: " + code
        );
    }

    // 🔥 VERIFY OTP
    public void verifyOtp(Integer userId, String code) {

        OTPVerification otp = otpRepository
                .findTopByTempUserIdAndStatusOrderByCreatedAtDesc(userId,OTPStatus.GENERATED)
                .orElseThrow(() -> new RuntimeException("OTP introuvable"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otp.setStatus(OTPStatus.EXPIRED);
            otpRepository.save(otp);
            throw new RuntimeException("OTP expiré");
        }

        if (otp.getAttemptCount() >= otp.getMaxAttempts()) {
            otp.setStatus(OTPStatus.FAILED);
            otpRepository.save(otp);
            throw new RuntimeException("Trop de tentatives");
        }

        if (!passwordEncoder.matches(code, otp.getCodeHash())) {

            otp.setAttemptCount(otp.getAttemptCount() + 1);
            otpRepository.save(otp);

            throw new RuntimeException("Code incorrect");
        }

        otp.setStatus(OTPStatus.VALIDATED);
        otp.setValidatedAt(LocalDateTime.now());
        OTPVerification otpSaved = otpRepository.save(otp);
        auditLogService.log(AuditAction.VERIFY_OTP,"Otp",otpSaved.getIdOtp(),userId,null);
        plafondRequestService.create(
                otp.getTempUserId(),
                otp.getTempCardId(),
                otp.getTempLimit(),
                otp.getTempJustification()
        );
    }
}
