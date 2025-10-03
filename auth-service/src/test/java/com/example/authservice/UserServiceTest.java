package com.example.authservice;

import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.KafkaEventService;
import com.example.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaEventService kafkaEventService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterNewUser() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // When
        User result = userService.registerUser(email, password);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertNotNull(result.getPasswordHash());
        assertTrue(result.isEnabled());

        verify(userRepository).existsByEmail(email);
        verify(userRepository).save(any(User.class));
        verify(kafkaEventService).sendUserCreatedEvent(eq(1L), eq(email), anyString());
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // Given
        String email = "existing@example.com";
        String password = "password123";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> userService.registerUser(email, password));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldAuthenticateValidUser() {
        // Given
        String email = "test@example.com";
        String password = "correctPassword";
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        user.setEnabled(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.authenticateUser(email, password);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void shouldNotAuthenticateWithWrongPassword() {
        // Given
        String email = "test@example.com";
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hashPassword(correctPassword));
        user.setEnabled(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.authenticateUser(email, wrongPassword);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotAuthenticateDisabledUser() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        user.setEnabled(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.authenticateUser(email, password);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldHashPasswordConsistently() {
        // Given
        String password = "testPassword123";

        // When
        String hash1 = hashPassword(password);
        String hash2 = hashPassword(password);

        // Then
        assertEquals(hash1, hash2);
        assertNotNull(hash1);
        assertEquals(64, hash1.length()); // SHA-256 produces 64-character hex string
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
