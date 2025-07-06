package com.example.template.config;

import static org.junit.jupiter.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.template.config.PerformanceMonitoringConfig.BusinessMetrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

/**
 * Tests for PerformanceMonitoringConfig to verify custom metrics are collected properly.
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
@SpringBootTest
class PerformanceMonitoringConfigTest {

    private MeterRegistry meterRegistry;
    
    @Mock
    private DataSource dataSource;
    
    private BusinessMetrics businessMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        businessMetrics = new BusinessMetrics(meterRegistry, dataSource);
    }

    @Test
    void testCustomMetricsAreCollected() {
        // Test that all expected metrics are registered
        assertNotNull(meterRegistry.find("auth.login.attempts").counter(), 
                "Login attempts counter should be registered");
        assertNotNull(meterRegistry.find("auth.login.successes").counter(), 
                "Login successes counter should be registered");
        assertNotNull(meterRegistry.find("auth.login.failures").counter(), 
                "Login failures counter should be registered");
        assertNotNull(meterRegistry.find("auth.login.duration").timer(), 
                "Authentication timer should be registered");
        assertNotNull(meterRegistry.find("external.api.calls").counter(), 
                "External API calls counter should be registered");
        assertNotNull(meterRegistry.find("external.api.duration").timer(), 
                "External API timer should be registered");
        assertNotNull(meterRegistry.find("circuit.breaker.trips").counter(), 
                "Circuit breaker trips counter should be registered");
    }

    @Test
    void testBusinessMetricsRecording() {
        // Test login metrics
        businessMetrics.recordLoginAttempt();
        businessMetrics.recordLoginSuccess();
        businessMetrics.recordLoginFailure();
        
        Counter loginAttempts = meterRegistry.find("auth.login.attempts").counter();
        Counter loginSuccesses = meterRegistry.find("auth.login.successes").counter();
        Counter loginFailures = meterRegistry.find("auth.login.failures").counter();
        
        assertNotNull(loginAttempts);
        assertNotNull(loginSuccesses);
        assertNotNull(loginFailures);
        
        assertEquals(1.0, loginAttempts.count(), "Should record 1 login attempt");
        assertEquals(1.0, loginSuccesses.count(), "Should record 1 login success");
        assertEquals(1.0, loginFailures.count(), "Should record 1 login failure");
    }

    @Test
    void testTimerMetrics() {
        // Test authentication timer
        Timer.Sample authSample = businessMetrics.startAuthenticationTimer();
        assertNotNull(authSample, "Authentication timer sample should not be null");
        
        // Simulate some processing time
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        businessMetrics.recordAuthenticationTime(authSample);
        
        Timer authTimer = meterRegistry.find("auth.login.duration").timer();
        assertNotNull(authTimer);
        assertEquals(1, authTimer.count(), "Should record 1 authentication timing");
        assertTrue(authTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) > 0, 
                "Authentication timer should record positive time");
    }

    @Test
    void testExternalApiMetrics() {
        // Test external API metrics
        businessMetrics.recordExternalApiCall();
        businessMetrics.recordCircuitBreakerTrip();
        
        Counter apiCalls = meterRegistry.find("external.api.calls").counter();
        Counter circuitBreakerTrips = meterRegistry.find("circuit.breaker.trips").counter();
        
        assertNotNull(apiCalls);
        assertNotNull(circuitBreakerTrips);
        
        assertEquals(1.0, apiCalls.count(), "Should record 1 external API call");
        assertEquals(1.0, circuitBreakerTrips.count(), "Should record 1 circuit breaker trip");
    }

    @Test
    void testActiveUsersGauge() {
        // Test active users gauge
        Gauge activeUsersGauge = meterRegistry.find("auth.active.users").gauge();
        assertNotNull(activeUsersGauge, "Active users gauge should be registered");
        
        // Initial value should be 0
        assertEquals(0.0, activeUsersGauge.value(), "Initial active users should be 0");
        
        // Increment and test
        businessMetrics.incrementActiveUsers();
        assertEquals(1.0, activeUsersGauge.value(), "Active users should be 1 after increment");
        
        // Decrement and test
        businessMetrics.decrementActiveUsers();
        assertEquals(0.0, activeUsersGauge.value(), "Active users should be 0 after decrement");
    }

    @Test
    void testDatabaseConnectionsGauge() {
        // Test database connections gauge
        Gauge dbConnectionsGauge = meterRegistry.find("database.connections.active").gauge();
        assertNotNull(dbConnectionsGauge, "Database connections gauge should be registered");
        
        // Initial value should be 0
        assertEquals(0.0, dbConnectionsGauge.value(), "Initial database connections should be 0");
        
        // Set a value and test
        businessMetrics.setDatabaseConnections(5);
        assertEquals(5.0, dbConnectionsGauge.value(), "Database connections should be 5 after setting");
    }

    @Test
    void testVirtualThreadsGauge() {
        // Test virtual threads gauge
        Gauge virtualThreadsGauge = meterRegistry.find("jvm.threads.virtual.count").gauge();
        assertNotNull(virtualThreadsGauge, "Virtual threads gauge should be registered");
        
        // The actual count depends on the runtime, so just verify it's non-negative
        assertTrue(virtualThreadsGauge.value() >= 0, "Virtual threads count should be non-negative");
    }
}
