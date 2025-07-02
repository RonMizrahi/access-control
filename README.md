# Spring Boot 3.5 Service Template

A production-ready Spring Boot 3.5 template implementing modern microservices patterns with virtual threads, distributed tracing, and comprehensive observability.

## 🚀 Features

### Core Security & Authentication
- **JWT Authentication**: Secure login with JSON Web Tokens using jjwt 0.12.6
- **Role-Based Access Control**: Restrict endpoints with `@IAdminRole` and `@IUserRole` annotations
- **Rate Limiting**: Built-in request rate limiting with Bucket4j
- **Security Configuration**: Production-ready Spring Security setup

### Spring Boot 3.5 & Modern Java
- **Virtual Threads**: High-performance I/O with Java 24 virtual threads (`spring.threads.virtual.enabled=true`)
- **Observability**: Distributed tracing with Micrometer and OpenTelemetry
- **Health Monitoring**: Production-ready actuator endpoints with custom health indicators
- **Modern Architecture**: Built for cloud-native microservices deployment

### Monitoring & Observability
- **Distributed Tracing**: Automatic span creation with `@Observed` annotations
- **Health Checks**: Custom database health indicators at `/actuator/health`
- **Application Info**: Comprehensive application metadata at `/actuator/info`
- **Correlation IDs**: Request correlation across logs and traces

## 🛠 Technologies

- **Java 24** with Virtual Threads
- **Spring Boot 3.5.3** with latest features
- **Spring Security 6.x** with JWT authentication
- **Micrometer Tracing** with OpenTelemetry bridge
- **Spring Boot Actuator** for monitoring
- **H2 Database** (development) / JPA for data persistence
- **Bucket4j** for rate limiting
- **Maven** for build management

## 📡 API Endpoints

### Authentication
- `POST /auth/login`: Authenticate user and receive JWT token
  ```json
  {
    "username": "admin",
    "password": "admin123"
  }
  ```

### Protected Resources
- `GET /auth/admin-roles`: Admin-only endpoint (requires `@IAdminRole`)
- `GET /auth/user-roles`: User-accessible endpoint (requires `@IUserRole`)

### Monitoring & Health
- `GET /actuator/health`: Application health status with database connectivity
- `GET /actuator/info`: Application metadata and build information

## 🔧 Configuration

### Virtual Threads
```properties
# Enable virtual threads for improved I/O performance
spring.threads.virtual.enabled=true
```

### Distributed Tracing
```properties
# Enable automatic tracing with correlation IDs
management.tracing.enabled=true
management.tracing.sampling.probability=1.0
logging.pattern.level=%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]
```

### Health Monitoring
```properties
# Expose health and info endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
management.endpoint.health.show-components=always
```

## 🚀 Getting Started

### Prerequisites

- **Java 24+** (for virtual threads support)
- **Maven 3.8+**

### Quick Start

1. **Clone the repository**:
   ```bash
   git clone <repo-url>
   cd spring-service-template
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Verify the setup**:
   ```bash
   # Check application health
   curl http://localhost:8080/actuator/health
   
   # Get application info
   curl http://localhost:8080/actuator/info
   ```

### 🔑 Default Credentials

The application starts with a default admin user (Feature flag enabled):
- **Username**: `admin`
- **Password**: `password`

⚠️ **Change the default password in production environments!**

## 💡 Usage Examples

### 1. User Authentication
```bash
# Login and get JWT token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### 2. Access Protected Endpoints
```bash
# Use JWT token for authenticated requests
curl -X GET http://localhost:8080/auth/admin-roles \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. Monitor Application Health
```bash
# Check comprehensive health status
curl http://localhost:8080/actuator/health

# Response includes database connectivity and custom health indicators
{
  "status": "UP",
  "components": {
    "database": {
      "status": "UP",
      "details": {
        "connection": "valid",
        "database": "H2"
      }
    }
  }
}
```

## 🔍 Observability Features

### Distributed Tracing
- **Automatic Spans**: Service methods annotated with `@Observed` create automatic traces
- **Correlation IDs**: All logs include `traceId` and `spanId` for request correlation
- **User Context**: Traces include authenticated user information

### Example: Traced Login Request
```bash
# Login request automatically creates distributed trace
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

**Log Output with Tracing**:
```
INFO  [accesscontrol,664d0c5c8b4b4c4d,664d0c5c8b4b4c4d] Starting span: auth.login with trace ID: 664d0c5c8b4b4c4d
INFO  [accesscontrol,664d0c5c8b4b4c4d,664d0c5c8b4b4c4d] Completed span: auth.login successfully
```

## 🏗 Architecture

### Spring Boot 3.5 Features
- **Virtual Threads**: Enabled via `spring.threads.virtual.enabled=true` for high-concurrency I/O operations
- **Observability Stack**: Micrometer + OpenTelemetry for comprehensive monitoring
- **Actuator Integration**: Production-ready health checks and application monitoring
- **Auto-Configuration**: Minimal manual configuration with Spring Boot's auto-configuration

### Project Structure
```
src/main/java/com/example/accesscontrol/
├── config/           # Configuration beans (Security, Virtual Threads, etc.)
├── controller/       # REST controllers with role-based access
├── interceptor/      # Rate limiting and request processing
├── model/           # JPA entities and data models
├── repository/      # Data access layer
└── service/         # Business logic with distributed tracing
```

### Key Components
- **VirtualThreadConfig**: Configures virtual thread executor for async operations
- **SecurityConfig**: JWT-based security with role annotations
- **DatabaseHealthIndicator**: Custom health check for database connectivity
- **AuthService**: Business logic with `@Observed` tracing annotations

## 🔄 Microservices Ready

This template is designed for microservices deployment with:
- **Service Discovery**: Ready for integration with service registries
- **API Gateway**: Compatible with Spring Cloud Gateway patterns
- **Distributed Tracing**: Built-in correlation across service boundaries
- **Health Checks**: Kubernetes-ready liveness and readiness probes
- **Configuration**: Externalized configuration support

## 🧪 Testing

Run the complete test suite:
```bash
mvn test
```

### Test Coverage
- **Unit Tests**: Service layer business logic
- **Integration Tests**: Full authentication flow
- **Security Tests**: Role-based access control
- **Health Check Tests**: Custom health indicators

## ⚡ Performance

### Virtual Threads Benefits
- **High Concurrency**: Handles thousands of concurrent requests with minimal memory overhead
- **I/O Optimization**: Non-blocking I/O operations for database and external service calls
- **Scalability**: Better resource utilization compared to traditional thread pools

### Monitoring Performance
Monitor virtual thread usage and application performance:
```bash
# Application metrics
curl http://localhost:8080/actuator/info

# Check virtual thread configuration
curl http://localhost:8080/actuator/health
```

## 🔧 Production Deployment

### Configuration for Production
```properties
# Production tracing settings (reduce sampling)
management.tracing.sampling.probability=0.1

# Security settings
security.jwt.secret=${JWT_SECRET}
security.jwt.expiration-ms=3600000

# Database configuration
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### Environment Variables
Set the following environment variables for production:
- `JWT_SECRET`: Strong secret key for JWT signing
- `DATABASE_URL`: Production database connection string
- `DB_USERNAME` / `DB_PASSWORD`: Database credentials

## 📊 Roadmap

Next planned features (see `scratchpads/spring-boot-35-template-plan.md`):
- [ ] **Unit 4**: Async Processing with event-driven patterns
- [ ] **Unit 5**: Circuit Breaker resilience patterns
- [ ] **Unit 6**: API Gateway integration features
- [ ] **Unit 7**: Performance monitoring dashboard
- [ ] **Unit 8**: Cloud-native configuration management

## 📄 License

MIT License
