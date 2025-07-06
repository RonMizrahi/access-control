package com.example.accesscontrol.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Integration tests for metrics endpoint accessibility and functionality.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringJUnitConfig
class MetricsEndpointAccessibilityTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testMetricsEndpointAccessible() {
        // Test that the metrics endpoint is accessible
        String url = "http://localhost:" + port + "/actuator/metrics";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode(), 
                "Metrics endpoint should be accessible");
        
        String responseBody = response.getBody();
        assertNotNull(responseBody, "Metrics endpoint should return a body");
        assertTrue(responseBody.contains("names"), 
                "Metrics endpoint should contain metric names");
    }

    @Test
    void testCustomMetricsExposed() {
        // Test that custom metrics are exposed
        String[] expectedMetrics = {
            "auth.login.attempts",
            "auth.login.successes", 
            "auth.login.failures",
            "auth.login.duration",
            "external.api.calls",
            "external.api.duration",
            "circuit.breaker.trips",
            "auth.active.users",
            "database.connections.active",
            "jvm.threads.virtual.count"
        };
        
        for (String metricName : expectedMetrics) {
            String url = "http://localhost:" + port + "/actuator/metrics/" + metricName;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            assertEquals(HttpStatus.OK, response.getStatusCode(), 
                    "Custom metric " + metricName + " should be accessible");
            assertNotNull(response.getBody(), 
                    "Custom metric " + metricName + " should return a body");
        }
    }

    @Test
    void testJvmMetricsExposed() {
        // Test that JVM metrics are exposed
        String[] jvmMetrics = {
            "jvm.memory.used",
            "jvm.memory.max", 
            "jvm.gc.pause",
            "jvm.threads.live",
            "system.cpu.usage",
            "process.uptime"
        };
        
        for (String metricName : jvmMetrics) {
            String url = "http://localhost:" + port + "/actuator/metrics/" + metricName;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            assertEquals(HttpStatus.OK, response.getStatusCode(), 
                    "JVM metric " + metricName + " should be accessible");
            assertNotNull(response.getBody(), 
                    "JVM metric " + metricName + " should return a body");
        }
    }
}
