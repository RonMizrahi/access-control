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

## Getting Started

### Prerequisites

- Java 17+
- Maven

### Setup

1. Clone the repository:
   ```sh
   git clone <repo-url>
   cd access-control
   ```

2. Build the project:
   ```sh
   mvn clean install
   ```

3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

### Usage

1. **Login**  
   Send a POST request to `/auth/login` with JSON body:
   ```json
   {
     "username": "your-username",
     "password": "your-password"
   }
   ```
   The response will include a JWT token.

2. **Access Protected Endpoints**  
   Use the JWT token in the `Authorization: Bearer <token>` header to access `/auth/admin-roles` or `/auth/user-roles` as appropriate.

## License

MIT License
