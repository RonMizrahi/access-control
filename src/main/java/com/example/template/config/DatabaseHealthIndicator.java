package com.example.template.config;

import javax.sql.DataSource;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom health indicator for database connectivity.
 * 
 * <p>
 * This health indicator verifies database connectivity by executing a simple
 * query.
 * It provides detailed information about database status for monitoring
 * purposes.
 * </p>
 * 
 * <p>
 * Health status will be UP if database is accessible, DOWN otherwise.
 * </p>
 * 
 * @author Backend Architect
 * @since 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    /**
     * Performs health check by testing database connectivity.
     * 
     * <p>
     * Executes a simple validation query to verify database is accessible.
     * Returns UP status with connection details if successful, DOWN status if
     * failed.
     * </p>
     * 
     * @return Health status with database connection details
     */
    @Override
    public Health health() {
        try {
            // Test database connection with a simple query
            try (var connection = dataSource.getConnection()) {
                var isValid = connection.isValid(1); // 1 second timeout

                if (isValid) {
                    log.debug("Database health check successful");
                    return Health.up()
                            .withDetail("database", "Available")
                            .withDetail("connection", "Valid")
                            .withDetail("url", connection.getMetaData().getURL())
                            .build();
                } else {
                    log.warn("Database connection validation failed");
                    return Health.down()
                            .withDetail("database", "Connection validation failed")
                            .build();
                }
            }
        } catch (Exception e) {
            log.error("Database health check failed", e);
            return Health.down()
                    .withDetail("database", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
