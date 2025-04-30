package com.example.accesscontrol.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.accesscontrol.model.Role;
import com.example.accesscontrol.model.SubscriptionPlan;
import com.example.accesscontrol.model.User;
import com.example.accesscontrol.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class RateLimitInterceptorTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private RateLimitInterceptor rateLimitInterceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Allows request when under rate limit")
    void preHandle_allowsRequest() throws Exception {
        User user = new User(1L, "user1", "pass", Set.of(Role.USER), SubscriptionPlan.BASIC);
        when(userDetails.getUsername()).thenReturn("user1");
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userDetails, null));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    @DisplayName("Blocks request when over rate limit")
    void preHandle_blocksWhenOverLimit() throws Exception {
        User user = new User(1L, "user1", "pass", Set.of(Role.USER), SubscriptionPlan.FREE);
        when(userDetails.getUsername()).thenReturn("user1");
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userDetails, null));
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);
        // Exhaust the bucket
        for (int i = 0; i < 3; i++) {
            rateLimitInterceptor.preHandle(request, response, new Object());
        }
        boolean result = rateLimitInterceptor.preHandle(request, response, new Object());
        assertFalse(result);
    }
}
