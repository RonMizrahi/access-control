# Circuit Breaker Implementation - Auto-Configuration Documentation

## What is Automatically Applied by Spring Boot and resilience4j-spring-boot3

### 1. Auto-Configuration Classes
When you add `resilience4j-spring-boot3` dependency, Spring Boot automatically configures:

- **CircuitBreakerAutoConfiguration**: Configures circuit breaker beans
- **CircuitBreakerMetricsAutoConfiguration**: Enables metrics collection
- **CircuitBreakerHealthIndicatorAutoConfiguration**: Health indicators for circuit breakers
- **ResilienceAspectsAutoConfiguration**: AOP aspects for annotations

### 2. Actuator Integration (Automatically Enabled)

#### Endpoints:
- `/actuator/circuitbreakers` - Shows circuit breaker states and metrics
- `/actuator/health` - Includes circuit breaker health indicators
- `/actuator/metrics` - Circuit breaker metrics available under `resilience4j.circuitbreaker.*`

#### Metrics Available:
- `resilience4j.circuitbreaker.calls.total` - Total number of calls
- `resilience4j.circuitbreaker.calls.failed` - Number of failed calls
- `resilience4j.circuitbreaker.calls.successful` - Number of successful calls
- `resilience4j.circuitbreaker.state` - Current circuit breaker state (0=CLOSED, 1=OPEN, 2=HALF_OPEN)

### 3. Annotation Support (Automatically Enabled)

#### Available Annotations:
- `@CircuitBreaker(name = "serviceName", fallbackMethod = "fallbackMethodName")`
- `@Retry(name = "serviceName")`
- `@RateLimiter(name = "serviceName")`
- `@Bulkhead(name = "serviceName")`
- `@TimeLimiter(name = "serviceName")`

### 4. Default Circuit Breaker Settings

When no configuration is provided, these defaults are applied:
```properties
# Default values (applied automatically)
resilience4j.circuitbreaker.instances.default.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.default.sliding-window-size=100
resilience4j.circuitbreaker.instances.default.minimum-number-of-calls=100
resilience4j.circuitbreaker.instances.default.wait-duration-in-open-state=60s
resilience4j.circuitbreaker.instances.default.permitted-number-of-calls-in-half-open-state=10
resilience4j.circuitbreaker.instances.default.sliding-window-type=COUNT_BASED
resilience4j.circuitbreaker.instances.default.automatic-transition-from-open-to-half-open-enabled=false
```

### 5. Health Indicators (Automatically Configured)

Circuit breaker health indicators are automatically registered and show:
- Current state of each circuit breaker
- Failure rate
- Number of calls in the current sliding window

### 6. Micrometer Integration (Automatic)

When Micrometer is on the classpath (which it is with Spring Boot Actuator):
- Metrics are automatically exported to the configured monitoring system
- Time-series data for circuit breaker events
- Integration with Prometheus, CloudWatch, etc.

### 7. Bean Registration (Automatic)

Spring Boot automatically creates and registers:
- `CircuitBreakerRegistry` bean
- `CircuitBreakerConfigCustomizer` beans
- Individual `CircuitBreaker` beans for each configured instance

## What You Need to Configure

### 1. Dependency (Required)
```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.2.0</version>
</dependency>
```

### 2. Application Properties (Optional - for customization)
```properties
# Custom circuit breaker settings
resilience4j.circuitbreaker.instances.externalApi.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.externalApi.sliding-window-size=10
# ... other custom settings
```

### 3. Actuator Endpoints (Optional - to expose metrics)
```properties
management.endpoints.web.exposure.include=health,info,circuitbreakers
management.endpoint.circuitbreakers.enabled=true
```

## Example Usage

### Service Method with Circuit Breaker
```java
@Service
public class ExternalApiService {
    
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackResponse")
    public String callExternalApi(String endpoint) {
        // External API call logic
        return restTemplate.getForObject(endpoint, String.class);
    }
    
    public String fallbackResponse(String endpoint, Throwable throwable) {
        return "Service temporarily unavailable";
    }
}
```

## Monitoring Circuit Breakers

1. **Actuator Endpoint**: `GET /actuator/circuitbreakers`
2. **Health Endpoint**: `GET /actuator/health` (includes circuit breaker status)
3. **Metrics**: `GET /actuator/metrics/resilience4j.circuitbreaker.calls.total`
4. **Logs**: Circuit breaker state changes are automatically logged

## No Manual Configuration Required

Unlike older versions of Resilience4j, you do NOT need to:
- Create `@Configuration` classes
- Manually define `CircuitBreaker` beans
- Set up metrics collection
- Configure health indicators
- Register AOP aspects

Everything is handled automatically by Spring Boot's auto-configuration!
