package com.example.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.config.IAdminRole;
import com.example.template.service.ExternalApiService;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller to demonstrate circuit breaker functionality
 */
@RestController
@RequestMapping("/api/demo")
@Slf4j
public class CircuitBreakerDemoController {

    @Autowired
    private ExternalApiService externalApiService;

    /**
     * Demo endpoint to test external API calls with circuit breaker
     * @param endpoint Optional endpoint to call (defaults to a mock endpoint)
     * @return Response from external API or fallback
     */
    @GetMapping("/external-api")
    @IAdminRole
    public ResponseEntity<String> callExternalApi(@RequestParam(defaultValue = "https://httpbin.org/get") String endpoint) {
        log.info("Demo: Calling external API with circuit breaker protection");
        try {
            String result = externalApiService.callExternalApi(endpoint);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Demo: Error calling external API", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Demo endpoint to test a failing service circuit breaker
     * @return Always triggers circuit breaker fallback
     */
    @GetMapping("/failing-service")
    @IAdminRole
    public ResponseEntity<String> callFailingService() {
        log.info("Demo: Calling failing service to demonstrate circuit breaker");
        String result = externalApiService.callFailingService();
        return ResponseEntity.ok(result);
    }

    /**
     * Get circuit breaker information
     * @return Information about circuit breaker usage
     */
    @GetMapping("/circuit-breaker-info")
    @IAdminRole
    public ResponseEntity<String> getCircuitBreakerInfo() {
        return ResponseEntity.ok(
            "Circuit Breaker Demo Endpoints:\n" +
            "- GET /api/demo/external-api?endpoint=<url> - Test external API with circuit breaker\n" +
            "- GET /api/demo/failing-service - Test always-failing service\n" +
            "- GET /actuator/circuitbreakers - View circuit breaker metrics\n" +
            "\nTry calling the failing-service endpoint multiple times to see the circuit breaker open!"
        );
    }
}
