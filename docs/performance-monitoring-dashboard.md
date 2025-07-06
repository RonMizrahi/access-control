# Spring Boot Template - Performance Monitoring Dashboard Configuration

## Overview
This document describes the custom metrics available in the Spring Boot Template and provides configuration examples for monitoring dashboards.

## Custom Business Metrics

### Authentication Metrics
- **auth.login.attempts**: Total number of login attempts
- **auth.login.successes**: Number of successful login attempts  
- **auth.login.failures**: Number of failed login attempts
- **auth.login.duration**: Time taken for authentication (Timer)
- **auth.active.users**: Number of currently active users (Gauge)

### External API Metrics
- **external.api.calls**: Total number of external API calls
- **external.api.duration**: Time taken for external API calls (Timer)

### Circuit Breaker Metrics
- **circuit.breaker.trips**: Number of times circuit breaker has opened

### Database Metrics
- **database.connections.active**: Number of active database connections (custom)
- **database.connections.pool.total**: Total database connections in pool (HikariCP)
- **database.connections.pool.active**: Active database connections in pool (HikariCP)
- **database.connections.pool.idle**: Idle database connections in pool (HikariCP)

### JVM and System Metrics
- **jvm.threads.virtual.count**: Number of virtual threads (Spring Boot 3.x specific)
- **jvm.memory.used**: JVM memory usage
- **jvm.memory.max**: JVM maximum memory
- **jvm.gc.pause**: Garbage collection pause times
- **jvm.threads.live**: Number of live threads
- **system.cpu.usage**: CPU usage percentage
- **process.uptime**: Application uptime

## Grafana Dashboard Configuration

### Authentication Panel
```json
{
  "title": "Authentication Metrics",
  "type": "stat",
  "targets": [
    {
      "expr": "auth_login_attempts_total",
      "legendFormat": "Total Attempts"
    },
    {
      "expr": "auth_login_successes_total",
      "legendFormat": "Successes"
    },
    {
      "expr": "auth_login_failures_total", 
      "legendFormat": "Failures"
    }
  ]
}
```

### Performance Panel
```json
{
  "title": "Response Times",
  "type": "graph",
  "targets": [
    {
      "expr": "auth_login_duration_seconds",
      "legendFormat": "Auth Duration"
    },
    {
      "expr": "external_api_duration_seconds",
      "legendFormat": "API Duration"
    }
  ]
}
```

### Database Health Panel
```json
{
  "title": "Database Connections",
  "type": "graph",
  "targets": [
    {
      "expr": "database_connections_pool_total",
      "legendFormat": "Total Pool"
    },
    {
      "expr": "database_connections_pool_active",
      "legendFormat": "Active"
    },
    {
      "expr": "database_connections_pool_idle",
      "legendFormat": "Idle"
    }
  ]
}
```

### JVM Health Panel
```json
{
  "title": "JVM Metrics",
  "type": "graph", 
  "targets": [
    {
      "expr": "jvm_memory_used_bytes",
      "legendFormat": "Memory Used"
    },
    {
      "expr": "jvm_threads_live",
      "legendFormat": "Threads"
    },
    {
      "expr": "jvm_threads_virtual_count",
      "legendFormat": "Virtual Threads"
    }
  ]
}
```

## Prometheus Configuration

Add to prometheus.yml:
```yaml
scrape_configs:
  - job_name: 'spring-boot-template'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

## Alerting Rules

### High Error Rate Alert
```yaml
groups:
  - name: spring-boot-template
    rules:
      - alert: HighLoginFailureRate
        expr: rate(auth_login_failures_total[5m]) > 0.1
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High login failure rate detected"
          description: "Login failure rate is {{ $value }} per second"
```

### Database Connection Alert
```yaml
      - alert: DatabaseConnectionPoolExhaustion
        expr: database_connections_pool_active / database_connections_pool_total > 0.8
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Database connection pool near exhaustion"
          description: "{{ $value }}% of database connections are in use"
```

## API Endpoints

### Metrics Endpoint
- **URL**: `/actuator/metrics`
- **Description**: Lists all available metrics
- **Authentication**: Required (admin role)

### Specific Metric
- **URL**: `/actuator/metrics/{metric.name}`
- **Description**: Returns specific metric details
- **Example**: `/actuator/metrics/auth.login.attempts`

### Health with Metrics
- **URL**: `/actuator/health`
- **Description**: Application health including custom indicators
- **Authentication**: Required (admin role for details)

## Performance Baselines

### Expected Values (baseline for 100 concurrent users)
- **auth.login.duration**: < 200ms (95th percentile)
- **external.api.duration**: < 500ms (95th percentile)  
- **auth.login.failures**: < 5% of total attempts
- **database.connections.pool.active**: < 80% of total pool
- **jvm.memory.used**: < 80% of max heap
- **system.cpu.usage**: < 70% average

### Scaling Thresholds
- **Scale up** when:
  - CPU usage > 70% for 5 minutes
  - Memory usage > 80% for 3 minutes
  - Database connections > 80% for 2 minutes
  - Response time > 500ms (95th percentile) for 5 minutes

- **Scale down** when:
  - CPU usage < 30% for 10 minutes
  - Memory usage < 50% for 10 minutes
  - All response times < 200ms (95th percentile) for 10 minutes

## Troubleshooting

### Common Issues
1. **High login failure rate**: Check for brute force attacks or authentication service issues
2. **Database connection exhaustion**: Investigate slow queries or connection leaks
3. **High response times**: Check external dependencies and GC pressure
4. **Circuit breaker trips**: Verify external service health and timeouts

### Debug Commands
```bash
# Check current metrics
curl -u admin:admin123 http://localhost:8080/actuator/metrics

# Check specific authentication metrics  
curl -u admin:admin123 http://localhost:8080/actuator/metrics/auth.login.attempts

# Check database metrics
curl -u admin:admin123 http://localhost:8080/actuator/metrics/database.connections.pool.active
```
