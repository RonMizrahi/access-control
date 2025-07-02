package com.example.accesscontrol.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.accesscontrol.config.JwtUtil;
import com.example.accesscontrol.model.User;
import com.example.accesscontrol.repository.UserRepository;

import io.micrometer.observation.annotation.Observed;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Observed(name = "auth.login", contextualName = "user-login")
    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
                return token;
            }
        }
        throw new BadCredentialsException("Invalid username or password");
    }
}
