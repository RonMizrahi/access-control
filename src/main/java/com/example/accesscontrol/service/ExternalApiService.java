package com.example.accesscontrol.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExternalApiService {

    @Autowired
    private RestClient restClient;

    /**
     * Simulates calling an external API with circuit breaker protection
     * @param endpoint the external API endpoint to call
     * @return response from external API or fallback response
     */
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackResponse")
    @Observed(name = "external.api.call", contextualName = "external-api-request")
    public String callExternalApi(String endpoint) {
        log.info("Calling external API: {}", endpoint);
        
        try {
            // Simulate external API call - this could fail
            String response = restClient.get()
                    .uri(endpoint)
                    .retrieve()
                    .body(String.class);
            log.info("External API call successful");
            return response;
        } catch (Exception e) {
            log.error("External API call failed: {}", e.getMessage());
            throw e; // Let circuit breaker handle this
        }
    }

    /**
     * Fallback method called when circuit breaker is open or on failure
     * @param endpoint the endpoint that was being called
     * @param throwable the exception that caused the fallback
     * @return fallback response
     */
    public String fallbackResponse(String endpoint, Throwable throwable) {
        log.warn("Circuit breaker fallback triggered for endpoint: {}, reason: {}", 
                endpoint, throwable.getMessage());
        
        return "Service temporarily unavailable. Please try again later.";
    }

    /**
     * Demo method to simulate a failing external service
     * @return always throws exception to test circuit breaker
     */
    @CircuitBreaker(name = "failingService", fallbackMethod = "failingServiceFallback")
    @Observed(name = "failing.service.call", contextualName = "failing-service-demo")
    public String callFailingService() {
        log.info("Calling failing service (demo)");
        
        throw new RuntimeException("Service is down for maintenance");
    }

    /**
     * Fallback for the failing service demo
     * @param throwable the exception that caused the fallback
     * @return fallback response
     */
    public String failingServiceFallback(Throwable throwable) {
        log.warn("Failing service fallback triggered: {}", throwable.getMessage());
        
        return "Demo service is currently unavailable - this is expected behavior for testing";
    }
}
