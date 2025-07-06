package com.example.accesscontrol.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.accesscontrol.config.PerformanceMonitoringConfig.BusinessMetrics;

import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;

/**
 * AOP Aspect for automatic performance monitoring of authentication and external API operations.
 * This aspect intercepts method calls and automatically records metrics without modifying business logic.
 */
@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {

    @Autowired
    private BusinessMetrics businessMetrics;

    /**
     * Intercepts login method calls to automatically record authentication metrics
     */
    @Around("execution(* com.example.accesscontrol.service.AuthService.login(..))")
    public Object monitorAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {
        // Record login attempt
        businessMetrics.recordLoginAttempt();
        
        // Start timing the authentication process
        Timer.Sample authTimer = businessMetrics.startAuthenticationTimer();
        
        try {
            // Execute the actual login method
            Object result = joinPoint.proceed();
            
            // If we reach here, login was successful
            businessMetrics.recordLoginSuccess();
            businessMetrics.incrementActiveUsers();
            
            return result;
        } catch (Exception e) {
            // Record failed login for any exception
            businessMetrics.recordLoginFailure();
            throw e; // Re-throw the exception
        } finally {
            // Record authentication time regardless of success/failure
            businessMetrics.recordAuthenticationTime(authTimer);
        }
    }

    /**
     * Intercepts external API calls to automatically record API metrics
     */
    @Around("execution(* com.example.accesscontrol.service.ExternalApiService.call*(..))")
    public Object monitorExternalApiCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        // Record external API call attempt
        businessMetrics.recordExternalApiCall();
        
        // Start timing the API call
        Timer.Sample apiTimer = businessMetrics.startExternalApiTimer();
        
        try {
            // Execute the actual API call method
            return joinPoint.proceed();
        } finally {
            // Record API call time regardless of success/failure
            businessMetrics.recordExternalApiTime(apiTimer);
        }
    }

    /**
     * Intercepts circuit breaker fallback methods to record circuit breaker trips
     */
    @Around("execution(* com.example.accesscontrol.service.ExternalApiService.*Fallback(..))")
    public Object monitorCircuitBreakerFallbacks(ProceedingJoinPoint joinPoint) throws Throwable {
        // Record circuit breaker trip when fallback is called
        businessMetrics.recordCircuitBreakerTrip();
        
        // Execute the fallback method
        return joinPoint.proceed();
    }
}
