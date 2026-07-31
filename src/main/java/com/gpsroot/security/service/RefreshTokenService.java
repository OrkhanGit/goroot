package com.gpsroot.security.service;

import com.gpsroot.security.model.RefreshToken;
import com.gpsroot.security.model.Users;
import com.gpsroot.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final long REFRESH_TOKEN_DURATION_MS = 1000L * 60 * 60 * 24 * 30; // 30 gün

    public RefreshToken createRefreshToken(Users user) {
        // Köhnə refresh token-ləri silirik ki, hər login-də yenisi olsun
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token tapılmadı"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token etibarsızdır, yenidən daxil olun");
        }

        return refreshToken;
    }

//    public void revokeToken(String token) {
//        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
//            refreshToken.setRevoked(true);
//            refreshTokenRepository.save(refreshToken);
//        });
//    }
}