package com.gpsroot.security.service;

import com.gpsroot.security.dto.ForgotPasswordRequest;
import com.gpsroot.security.dto.ResetPasswordRequest;
import com.gpsroot.security.dto.VerifyCodeRequest;
import com.gpsroot.security.dto.VerifyCodeResponse;
import com.gpsroot.security.model.PasswordResetToken;
import com.gpsroot.security.model.Users;
import com.gpsroot.security.repository.PasswordResetTokenRepository;
import com.gpsroot.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int CODE_TTL_MINUTES = 10;
    private static final int RESET_TOKEN_TTL_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    public ResponseEntity<String> forgotPassword(ForgotPasswordRequest request) {
        // İstifadəçi mövcud olmasa belə eyni cavabı qaytarırıq (email enumeration-un qarşısını almaq üçün)
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String code = generateSixDigitCode();

            PasswordResetToken entry = PasswordResetToken.builder()
                    .email(request.getEmail())
                    .codeHash(passwordEncoder.encode(code))
                    .codeExpiresAt(LocalDateTime.now().plusMinutes(CODE_TTL_MINUTES))
                    .attempts(0)
                    .verified(false)
                    .used(false)
                    .build();

            resetTokenRepository.save(entry);
            emailService.sendResetCode(request.getEmail(), code);
        });

        return ResponseEntity.ok("Əgər bu email qeydiyyatdadırsa, kod göndərildi");
    }

    public VerifyCodeResponse verifyCode(VerifyCodeRequest request) {
        PasswordResetToken entry = resetTokenRepository
                .findTopByEmailOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kod tapılmadı"));

        if (entry.isUsed() || entry.getCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kodun vaxtı bitib");
        }

        if (entry.getAttempts() >= MAX_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cəhd limiti aşıldı");
        }

        if (!passwordEncoder.matches(request.getCode(), entry.getCodeHash())) {
            entry.setAttempts(entry.getAttempts() + 1);
            resetTokenRepository.save(entry);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kod yanlışdır");
        }

        String resetToken = UUID.randomUUID().toString();
        entry.setVerified(true);
        entry.setResetToken(resetToken);
        entry.setResetTokenExpiresAt(LocalDateTime.now().plusMinutes(RESET_TOKEN_TTL_MINUTES));
        resetTokenRepository.save(entry);

        return VerifyCodeResponse.builder().resetToken(resetToken).build();
    }

    public ResponseEntity<String> resetPassword(ResetPasswordRequest request) {
        PasswordResetToken entry = resetTokenRepository
                .findByResetToken(request.getResetToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token yanlışdır"));

        if (!entry.isVerified() || entry.isUsed()
                || entry.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token etibarsızdır");
        }

        Users user = userRepository.findByEmail(entry.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "İstifadəçi tapılmadı"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        entry.setUsed(true);
        resetTokenRepository.save(entry);

        return ResponseEntity.ok("Şifrə uğurla dəyişdirildi");
    }

    private String generateSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}