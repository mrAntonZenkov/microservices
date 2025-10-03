package com.example.authservice.service;

import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.User;
import com.example.authservice.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserService userService, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Map<String, String> login(String email, String password) {
        User user = userService.authenticateUser(email, password)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String accessToken = jwtService.generateAccessToken(user.getEmail(),
                Map.of("role", user.getRole().name()));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        saveRefreshToken(user, refreshToken);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "expiresIn", jwtService.getAccessTokenExpiration().toString()
        );
    }

    public Map<String, String> register(String email, String password) {
        User user = userService.registerUser(email, password);

        String accessToken = jwtService.generateAccessToken(user.getEmail(),
                Map.of("role", user.getRole().name()));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        saveRefreshToken(user, refreshToken);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "expiresIn", jwtService.getAccessTokenExpiration().toString()
        );
    }

    public Map<String, String> refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = storedToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user.getEmail(),
                Map.of("role", user.getRole().name()));
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        refreshTokenRepository.delete(storedToken);

        saveRefreshToken(user, newRefreshToken);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken,
                "expiresIn", jwtService.getAccessTokenExpiration().toString()
        );
    }

    private void saveRefreshToken(User user, String refreshToken) {
        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now()
                .plusSeconds(jwtService.getRefreshTokenExpiration() / 1000));

        refreshTokenRepository.save(token);
    }

    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}
