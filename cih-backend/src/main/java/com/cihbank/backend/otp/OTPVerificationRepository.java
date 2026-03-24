package com.cihbank.backend.otp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Integer> {
    Optional<OTPVerification> findTopByTempUserIdAndStatusOrderByCreatedAtDesc(
            Integer userId, OTPStatus status
    );
}
