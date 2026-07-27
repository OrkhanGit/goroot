package com.gpsroot.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    // 6 rəqəmli kodun hash-i (heç vaxt açıq saxlanmır)
    private String codeHash;

    private LocalDateTime codeExpiresAt;

    @Builder.Default
    private int attempts = 0;

    @Builder.Default
    private boolean verified = false;

    // verify-code addımından sonra generasiya olunan, reset üçün istifadə olunan token
    private String resetToken;

    private LocalDateTime resetTokenExpiresAt;

    @Builder.Default
    private boolean used = false;
}