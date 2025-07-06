package com.example.template.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 configuration for API Gateway integration.
 * Provides comprehensive API documentation with security schemas.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Spring Service Template API",
        version = "2.0",
        description = """
            Spring Boot 3.5 microservice template with comprehensive features:
            - JWT Authentication & Role-based Authorization
            - Virtual Threads for improved concurrency
            - Circuit Breaker pattern for resilience
            - Distributed tracing and observability
            - API versioning support
            - Kubernetes-ready health probes
            - API Gateway integration features
            """,
        contact = @Contact(
            name = "API Support",
            email = "support@example.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Local development server"
        ),
        @Server(
            url = "https://api.example.com",
            description = "Production server (behind API Gateway)"
        )
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT token authentication. Obtain token from /auth/login endpoint."
)
@SecurityScheme(
    name = "API Key",
    type = SecuritySchemeType.APIKEY,
    description = "API Key authentication for service-to-service communication via API Gateway"
)
public class OpenApiConfig {
    
    // Bean configuration is handled by springdoc-openapi-starter-webmvc-ui
    // Additional customization can be added here if needed
}
