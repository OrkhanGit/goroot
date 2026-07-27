package com.gpsroot.security.repository;

import com.gpsroot.security.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findTopByEmailOrderByIdDesc(String email);
    Optional<PasswordResetToken> findByResetToken(String resetToken);
}