package com.example.authservice;

import com.example.authservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "mySecretKeyForJWTGenerationWithMinimum256BitsLength123!");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 900000L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 86400000L);
    }

    @Test
    void shouldGenerateAndValidateAccessToken() {
        String username = "test@example.com";
        Map<String, Object> claims = Map.of("role", "ROLE_USER");

        String token = jwtService.generateAccessToken(username, claims);

        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
        assertTrue(jwtService.validateToken(token, username));
    }

    @Test
    void shouldGenerateRefreshToken() {
        String username = "test@example.com";

        String token = jwtService.generateRefreshToken(username);

        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String username = "test@example.com";
        String token = jwtService.generateAccessToken(username, Map.of());

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void shouldExtractExpirationFromToken() {
        String username = "test@example.com";
        String token = jwtService.generateAccessToken(username, Map.of());

        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void shouldExtractClaimFromToken() {
        String username = "test@example.com";
        Map<String, Object> claims = Map.of("role", "ROLE_ADMIN");
        String token = jwtService.generateAccessToken(username, claims);

        String role = jwtService.extractClaim(token, claimsMap -> claimsMap.get("role").toString());

        assertEquals("ROLE_ADMIN", role);
    }

    @Test
    void shouldReturnFalseForTokenWithDifferentUsername() {
        String username = "test@example.com";
        String differentUsername = "different@example.com";
        String token = jwtService.generateAccessToken(username, Map.of());

        assertFalse(jwtService.validateToken(token, differentUsername));
    }
}
