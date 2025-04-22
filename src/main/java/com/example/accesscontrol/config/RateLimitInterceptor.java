package com.example.accesscontrol.config;

import com.example.accesscontrol.model.User;
import com.example.accesscontrol.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    // Example rate limits per plan (requests per minute)
    private static final Map<String, Integer> PLAN_LIMITS = Map.of(
            "FREE", 10,
            "PRO", 100,
            "ENTERPRISE", 1000
    );

    // user -> [windowStart, count]
    private final Map<String, RateLimitInfo> userLimits = new ConcurrentHashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                String plan = user.getSubscriptionPlan();
                int limit = PLAN_LIMITS.getOrDefault(plan, 10);
                long now = Instant.now().getEpochSecond();
                long window = now / 60; // per minute
                RateLimitInfo info = userLimits.computeIfAbsent(username, k -> new RateLimitInfo(window, 0));
                synchronized (info) {
                    if (info.window != window) {
                        info.window = window;
                        info.count = 1;
                    } else {
                        info.count++;
                    }
                    if (info.count > limit) {
                        response.setStatus(429);
                        response.getWriter().write("Rate limit exceeded");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static class RateLimitInfo {
        long window;
        int count;
        RateLimitInfo(long window, int count) {
            this.window = window;
            this.count = count;
        }
    }
}
