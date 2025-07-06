# Docker Development Environment

This document describes how to use Docker and Docker Compose with the Spring Boot 3.5 template.

## Quick Start

### 1. Minimal Setup (H2 + Zipkin only)
```bash
# Build the application image
docker compose build

# Start minimal stack
docker compose --profile minimal up -d

# Check health
curl http://localhost:8080/actuator/health
```

### 2. Full Development Stack
```bash
# Start all services (PostgreSQL, Redis, Kafka, Zipkin + GUIs)
docker compose --profile full up -d

# Check all services are healthy
docker compose ps
```

### 3. Custom Profiles
```bash
# Just caching (Redis)
docker compose --profile cache up -d

# Just messaging (Kafka)
docker compose --profile messaging up -d

# Database + GUI tools
docker compose --profile postgres --profile gui up -d
```

## Available Services

| Service | URL | Description | Profile |
|---------|-----|-------------|---------|
| Spring Boot App | http://localhost:8080 | Main application | minimal, full |
| Zipkin UI | http://localhost:9411 | Distributed tracing | minimal, full, tracing |
| PostgreSQL | localhost:5432 | Database | full, postgres |
| Redis | localhost:6379 | Cache | full, cache |
| Kafka | localhost:9092 | Message broker | full, messaging |
| Redis Commander | http://localhost:8081 | Redis GUI | full, gui |
| Kafka UI | http://localhost:8082 | Kafka management | full, gui |
| Adminer | http://localhost:8083 | Database GUI | full, postgres, gui |

## Environment Configuration

### Using .env file
```bash
# Copy the example environment file
cp .env.example .env

# Edit .env to customize settings
nano .env
```

### PostgreSQL Configuration
To use PostgreSQL instead of H2:
```bash
# Set environment variables
export SPRING_PROFILES_ACTIVE=postgres
export SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/springdb
export SPRING_DATASOURCE_USERNAME=spring
export SPRING_DATASOURCE_PASSWORD=spring123

# Start with PostgreSQL
docker compose --profile postgres up -d
```

### Redis Configuration
Redis is automatically configured when using cache or full profiles:
```bash
# Redis will be available at redis:6379 from the app container
# localhost:6379 from host machine
```

### Kafka Configuration
Kafka is configured with auto-topic creation:
```bash
# Kafka will be available at kafka:9092 from app container
# localhost:9092 from host machine
```

## Development Workflow

### 1. Hot Reload Development
```bash
# Build the app locally first
./mvnw clean compile

# Start with development overrides
docker compose -f docker-compose.yml -f docker-compose.override.yml up -d

# Make code changes - Spring Boot DevTools will reload automatically
# Debug port 5005 is exposed for IDE debugging
```

### 2. Testing Different Configurations
```bash
# Test with H2 (minimal)
docker compose --profile minimal up -d
curl http://localhost:8080/actuator/health

# Test with PostgreSQL
docker compose --profile postgres up -d
# Update app to use postgres profile

# Test with full stack
docker compose --profile full up -d
```

### 3. Monitoring and Debugging
```bash
# View logs
docker compose logs -f app
docker compose logs -f postgres
docker compose logs -f kafka

# Check health of all services
docker compose ps

# Access service dashboards
open http://localhost:9411  # Zipkin traces
open http://localhost:8081  # Redis Commander
open http://localhost:8082  # Kafka UI
open http://localhost:8083  # Adminer (DB GUI)
```

## Useful Commands

### Container Management
```bash
# Start all services
docker compose --profile full up -d

# Stop all services
docker compose down

# Rebuild and restart app
docker compose up -d --build app

# View service status
docker compose ps

# Follow logs
docker compose logs -f

# Remove all containers and volumes
docker compose down -v
```

### Database Operations
```bash
# Connect to PostgreSQL
docker compose exec postgres psql -U spring -d springdb

# Backup database
docker compose exec postgres pg_dump -U spring springdb > backup.sql

# Restore database
docker compose exec -T postgres psql -U spring springdb < backup.sql
```

### Redis Operations
```bash
# Connect to Redis CLI
docker compose exec redis redis-cli

# Monitor Redis commands
docker compose exec redis redis-cli monitor

# Check Redis info
docker compose exec redis redis-cli info
```

### Kafka Operations
```bash
# List topics
docker compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# Create topic
docker compose exec kafka kafka-topics --bootstrap-server localhost:9092 --create --topic test-topic

# Send test message
docker compose exec kafka kafka-console-producer --bootstrap-server localhost:9092 --topic test-topic

# Consume messages
docker compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic test-topic --from-beginning
```

## Troubleshooting

### Common Issues

1. **Port conflicts**
   ```bash
   # Check what's using the ports
   netstat -tulpn | grep :8080
   netstat -tulpn | grep :5432
   
   # Stop conflicting services or change ports in docker-compose.yml
   ```

2. **Services not starting**
   ```bash
   # Check logs for errors
   docker compose logs service-name
   
   # Check if containers are healthy
   docker compose ps
   ```

3. **Database connection issues**
   ```bash
   # Ensure PostgreSQL is ready
   docker compose exec postgres pg_isready -U spring
   
   # Check database exists
   docker compose exec postgres psql -U spring -l
   ```

4. **Memory issues**
   ```bash
   # Increase Docker memory limits
   # Or reduce JVM memory usage in docker-compose.yml
   ```

### Reset Environment
```bash
# Stop and remove everything
docker compose down -v

# Remove all images
docker compose down --rmi all

# Start fresh
docker compose --profile full up -d --build
```

## Production Notes

- The `docker-compose.override.yml` file is for development only
- Use proper secrets management in production
- Configure health checks and monitoring
- Use external databases and message brokers in production
- Set appropriate resource limits and scaling policies
