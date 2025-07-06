package com.example.accesscontrol.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Configuration for comprehensive performance monitoring using Micrometer metrics.
 * Provides custom business metrics, JVM metrics, and database connection pool monitoring.
 */
@Configuration
public class PerformanceMonitoringConfig {



    /**
     * Custom business metrics registry component
     * Provides counters, timers, and gauges for business operations
     */
    @Component
    public static class BusinessMetrics {
        
        private final MeterRegistry meterRegistry;
        private final Counter loginAttempts;
        private final Counter loginSuccesses;
        private final Counter loginFailures;
        private final Timer authenticationTimer;
        private final Counter externalApiCalls;
        private final Timer externalApiTimer;
        private final Counter circuitBreakerTrips;
        private final AtomicLong activeUsers;
        private final AtomicLong databaseConnections;

        public BusinessMetrics(MeterRegistry meterRegistry, DataSource dataSource) {
            this.meterRegistry = meterRegistry;
            
            // Authentication metrics
            this.loginAttempts = Counter.builder("auth.login.attempts")
                    .description("Total number of login attempts")
                    .register(meterRegistry);
                    
            this.loginSuccesses = Counter.builder("auth.login.successes")
                    .description("Number of successful login attempts")
                    .register(meterRegistry);
                    
            this.loginFailures = Counter.builder("auth.login.failures")
                    .description("Number of failed login attempts")
                    .register(meterRegistry);
                    
            this.authenticationTimer = Timer.builder("auth.login.duration")
                    .description("Time taken for authentication")
                    .register(meterRegistry);

            // External API metrics
            this.externalApiCalls = Counter.builder("external.api.calls")
                    .description("Total number of external API calls")
                    .register(meterRegistry);
                    
            this.externalApiTimer = Timer.builder("external.api.duration")
                    .description("Time taken for external API calls")
                    .register(meterRegistry);

            // Circuit breaker metrics
            this.circuitBreakerTrips = Counter.builder("circuit.breaker.trips")
                    .description("Number of times circuit breaker has opened")
                    .register(meterRegistry);

            // Active session tracking
            this.activeUsers = new AtomicLong(0);
            Gauge.builder("auth.active.users", activeUsers, AtomicLong::get)
                    .description("Number of currently active users")
                    .register(meterRegistry);

            // Database connection pool monitoring
            this.databaseConnections = new AtomicLong(0);
            Gauge.builder("database.connections.active", databaseConnections, AtomicLong::get)
                    .description("Number of active database connections")
                    .register(meterRegistry);

            // JVM Virtual Thread metrics (Spring Boot 3.x specific)
            Gauge.builder("jvm.threads.virtual.count", this::getVirtualThreadCount)
                    .description("Number of virtual threads")
                    .register(meterRegistry);

            // Database connection pool health from DataSource
            try {
                if (dataSource instanceof com.zaxxer.hikari.HikariDataSource hikariDataSource) {
                    Gauge.builder("database.connections.pool.total", 
                            hikariDataSource.getHikariPoolMXBean(), 
                            pool -> pool != null ? pool.getTotalConnections() : 0)
                            .description("Total database connections in pool")
                            .register(meterRegistry);
                                    
                    Gauge.builder("database.connections.pool.active", 
                            hikariDataSource.getHikariPoolMXBean(), 
                            pool -> pool != null ? pool.getActiveConnections() : 0)
                            .description("Active database connections in pool")
                            .register(meterRegistry);
                                    
                    Gauge.builder("database.connections.pool.idle", 
                            hikariDataSource.getHikariPoolMXBean(), 
                            pool -> pool != null ? pool.getIdleConnections() : 0)
                            .description("Idle database connections in pool")
                            .register(meterRegistry);
                }
            } catch (Exception e) {
                // Fallback for non-HikariCP data sources
                // Just log and continue - the basic connection counter will still work
            }
        }

        // Business metric recording methods
        public void recordLoginAttempt() {
            loginAttempts.increment();
        }

        public void recordLoginSuccess() {
            loginSuccesses.increment();
        }

        public void recordLoginFailure() {
            loginFailures.increment();
        }

        public Timer.Sample startAuthenticationTimer() {
            return Timer.start(meterRegistry);
        }

        public void recordAuthenticationTime(Timer.Sample sample) {
            sample.stop(authenticationTimer);
        }

        public void recordExternalApiCall() {
            externalApiCalls.increment();
        }

        public Timer.Sample startExternalApiTimer() {
            return Timer.start(meterRegistry);
        }

        public void recordExternalApiTime(Timer.Sample sample) {
            sample.stop(externalApiTimer);
        }

        public void recordCircuitBreakerTrip() {
            circuitBreakerTrips.increment();
        }

        public void incrementActiveUsers() {
            activeUsers.incrementAndGet();
        }

        public void decrementActiveUsers() {
            activeUsers.decrementAndGet();
        }

        public void setDatabaseConnections(long count) {
            databaseConnections.set(count);
        }

        /**
         * Get virtual thread count using JMX (best effort)
         * @return number of virtual threads or 0 if unable to determine
         */
        private double getVirtualThreadCount() {
            try {
                // This is a best-effort attempt to count virtual threads
                // Virtual threads are not directly exposed via standard JMX
                var threadBean = ManagementFactory.getThreadMXBean();
                long[] allThreadIds = threadBean.getAllThreadIds();
                long virtualThreadCount = 0;
                
                for (long threadId : allThreadIds) {
                    var threadInfo = threadBean.getThreadInfo(threadId);
                    if (threadInfo != null && threadInfo.getThreadName().contains("virtual")) {
                        virtualThreadCount++;
                    }
                }
                
                return virtualThreadCount;
            } catch (Exception e) {
                // If we can't determine virtual thread count, return 0
                return 0;
            }
        }
    }
}
