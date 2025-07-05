package com.example.accesscontrol.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@SpringJUnitConfig
class ExternalApiServiceTest {

    @Autowired
    private ExternalApiService externalApiService;

    @Test
    void testFailingServiceAlwaysReturnsFallback() {
        // When
        String result = externalApiService.callFailingService();

        // Then
        assertEquals("Demo service is currently unavailable - this is expected behavior for testing", result);
    }

    @Test
    void testExternalApiCallWithInvalidEndpoint() {
        // Given - an invalid endpoint that will definitely fail
        String invalidEndpoint = "http://invalid-url-that-does-not-exist.com";

        // When
        String result = externalApiService.callExternalApi(invalidEndpoint);

        // Then - Should return fallback response
        assertEquals("Service temporarily unavailable. Please try again later.", result);
    }

    @Test
    void testCircuitBreakerOpensAfterRepeatedFailures() {
        // Given
        String invalidEndpoint = "http://another-invalid-url.com";

        // When - Call the service multiple times to trigger circuit breaker
        String result1 = externalApiService.callExternalApi(invalidEndpoint);
        String result2 = externalApiService.callExternalApi(invalidEndpoint);
        String result3 = externalApiService.callExternalApi(invalidEndpoint);

        // Then - All calls should return fallback response
        assertEquals("Service temporarily unavailable. Please try again later.", result1);
        assertEquals("Service temporarily unavailable. Please try again later.", result2);
        assertEquals("Service temporarily unavailable. Please try again later.", result3);
    }

    @Test
    void testSuccessfulExternalApiCall() {
        // Given - a real endpoint that should work (using httpbin.org for testing)
        String validEndpoint = "https://httpbin.org/get";

        // When
        String result = externalApiService.callExternalApi(validEndpoint);

        // Then - Should contain some response data (not fallback message)
        assertNotNull(result);
        assertFalse(result.contains("Service temporarily unavailable"));
    }
}
