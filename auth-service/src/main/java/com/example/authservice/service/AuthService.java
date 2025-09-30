package com.example.authservice.service;

import com.example.authservice.dto.AuthResponse;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.RefreshRequest;
import com.example.authservice.dto.UserRegisterRequest;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.Role;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final KafkaProducerService kafkaProducerService;


    public AuthResponse register(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(hashPassword(request.password()));
        user.setRoles(Role.ROLE_USER);
        user.setEnabled(false);

        User saved = userRepository.save(user);

        kafkaProducerService.publishUserCreated(saved);

        return generateTokens(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getPasswordHash().equals(hashPassword(request.password()))) {
            throw new IllegalArgumentException("Invalid password");
        }
        if (!user.isEnabled()) {
            throw new IllegalStateException("User is not active");
        }
        return generateTokens(user);
    }

    public AuthResponse refresh(RefreshRequest refreshRequest) {
        RefreshToken token = refreshTokenService.validateRefreshToken(refreshRequest.refreshToken());
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRoles().name()
        );

        return new AuthResponse(newAccessToken, refreshRequest.refreshToken());
    }

    private AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRoles().name()
        );

        RefreshToken savedToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(accessToken, savedToken.getToken());
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
