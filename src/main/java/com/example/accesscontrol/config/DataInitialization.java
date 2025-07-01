package com.example.accesscontrol.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.accesscontrol.model.Role;
import com.example.accesscontrol.model.SubscriptionPlan;
import com.example.accesscontrol.model.User;
import com.example.accesscontrol.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Configuration class responsible for initializing application data on startup.
 * Handles creation of default admin user when enabled through configuration.
 * Only active when 'app.init.add-admin=true' property is set.
 * 
 * @author Backend Architect
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(name = "app.init.add-admin", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class DataInitialization {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitialization.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Creates a default admin user if it doesn't already exist.
     * This method is called after the application context is ready.
     * Only executes when this configuration is active (app.init.add-admin=true).
     * 
     * The admin user is created with:
     * - Username: "admin"
     * - Password: "password"
     * - Role: ADMIN
     * - Subscription Plan: PROFESSIONAL
     */
    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultAdminUser() {
        logger.debug("Checking if default admin user creation is needed");
        
        if (userRepository.findByUsername("admin").isEmpty()) {
            logger.info("Creating default admin user");

            // Create admin role
            User adminUser = new User();
            adminUser.setUsername("test-admin");
            adminUser.setPassword(passwordEncoder.encode("password"));
            adminUser.setRoles(Set.of(Role.ADMIN));
            adminUser.setSubscriptionPlan(SubscriptionPlan.PROFESSIONAL);
            userRepository.save(adminUser);

            // Create user role
            // Create admin role
            User userUser = new User();
            userUser.setUsername("test-user");
            userUser.setPassword(passwordEncoder.encode("password"));
            userUser.setRoles(Set.of(Role.USER));
            userUser.setSubscriptionPlan(SubscriptionPlan.FREE);
            userRepository.save(userUser);

            logger.info("Default admin user created successfully with username: admin");
            logger.warn("Please change the default admin password immediately for security purposes");
        } else {
            logger.debug("Admin user already exists, skipping creation");
        }
    }
}
