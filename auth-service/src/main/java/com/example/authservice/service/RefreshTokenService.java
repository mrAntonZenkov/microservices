package com.example.authservice.service;

import com.example.authservice.entity.RefreshToken;
import com.example.authservice.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setRevoked(false);
        return refreshTokenRepository.save(token);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken rt = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (rt.isRevoked()) {
            throw new IllegalStateException("Refresh token revoked");
        }
        return rt;
    }

    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    public void revokeAllTokensForUser(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
