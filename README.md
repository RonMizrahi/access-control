# Spring Boot 3.5 Service Template

This is a modern, production-ready Spring Boot 3.5 template for building secure, observable, and scalable microservices.

**Key technologies and features:**
- Spring Boot 3.5 (Java 24) with virtual threads for high concurrency
- JWT authentication and role-based access control
- PostgreSQL for persistent storage (auto-initialized schema)
- Redis for caching
- Kafka for event-driven messaging
- Zipkin and Micrometer/Opentelemetry for distributed tracing
- Adminer, Redis Commander, and Kafka UI for local development
- Rate limiting (Bucket4j) and circuit breaker (Resilience4j)
- Swagger UI for API docs

All services are containerized with Docker Compose for easy local development and testing. Configuration is externalized and can be overridden per environment. This template is ideal for quickly bootstrapping new microservices or as a reference for best practices in Spring Boot cloud-native development.

## Quick Start

### Prerequisites
- Java 24+
- Docker & Docker Compose

### Run with Docker Compose (Recommended)
```sh
# Start all services (app + dependencies)
docker compose --profile full up --build -d

# Verify services are running
docker compose ps

# View app logs
docker compose logs -f app
```

**Services Available:**
- App: http://localhost:8080
- PostgreSQL: localhost:5432 (spring/spring123)
- Redis: localhost:6379
- Kafka: localhost:9092
- Zipkin: http://localhost:9411
- Adminer (DB GUI): http://localhost:8083
- Redis Commander: http://localhost:8081
- Kafka UI: http://localhost:8082

### Stop Services
```sh
# Stop all (keep data)
docker compose --profile full down

# Stop all (remove data)
docker compose --profile full down -v

# Stop single service
docker compose stop postgres
```

### Run Locally (Development)
```sh
# Build project
mvn clean install

# Run app only (requires local dependencies)
mvn spring-boot:run
```

## API Usage



### Default Admin User
By default, the application creates an admin user on startup if enabled in config:

```yaml
app:
  init:
    add-admin: true
```
```
  {
    "username":"test-admin",
    "password":"password"
}
```
### Authentication
```sh
# Login and get JWT token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test-admin", "password": "password"}'
```

### Health Check
```sh
curl http://localhost:8080/actuator/health
```

## Key Features
- **JWT Authentication** with role-based access
- **Virtual Threads** (Java 24) for high concurrency
- **Distributed Tracing** with Zipkin integration
- **PostgreSQL** with schema auto-initialization
- **Redis** caching
- **Kafka** messaging
- **Rate Limiting** with Bucket4j
- **Circuit Breaker** with Resilience4j
- **Swagger UI** at `/swagger-ui.html`

```sh
# Check if services are running
docker compose ps

# View logs
docker compose logs -f app

# If "no service selected" error
docker compose --profile full up --build -d

# Database connection issues
# Ensure SPRING_PROFILES_ACTIVE=postgres is set in docker-compose.override.yml
```

## Development Notes
- Uses profiles in docker-compose.yml - must specify `--profile full`
- Database schema auto-created from `docker/postgres/init-sql-schema.sql`
- Development override in `docker-compose.override.yml`
- Virtual threads enabled for high-concurrency I/O
