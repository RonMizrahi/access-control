package com.example.accesscontrol.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.accesscontrol.model.SubscriptionPlan;
import com.example.accesscontrol.model.User;
import com.example.accesscontrol.repository.UserRepository;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SubscriptionRateLimitInterceptor implements HandlerInterceptor {
    // user -> Bucket
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                SubscriptionPlan subscriptionPlan = user.getSubscriptionPlan();
                Bucket bucket = userBuckets.computeIfAbsent(username, k -> createBucket(subscriptionPlan));
                if (!bucket.tryConsume(1)) {
                    response.setStatus(429);
                    response.getWriter().write("Rate limit exceeded");
                    return false;
                }
            }
        }
        return true;
    }

    private Bucket createBucket(SubscriptionPlan plan) {
        Bandwidth limit = plan.getLimit();
        return Bucket.builder().addLimit(limit).build();
    }
}
