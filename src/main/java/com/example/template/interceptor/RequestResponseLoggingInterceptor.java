package com.example.template.interceptor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor for request/response logging with correlation ID handling.
 * Designed for API Gateway integration in Kubernetes environments.
 */
@Component
public class RequestResponseLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);
    
    // Standard correlation ID headers used by API gateways
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    
    // MDC keys for structured logging
    private static final String MDC_CORRELATION_ID = "correlationId";
    private static final String MDC_REQUEST_ID = "requestId";
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        
        // Extract or generate correlation ID
        String correlationId = extractCorrelationId(request);
        String requestId = extractRequestId(request);
        
        // Set in MDC for logging
        MDC.put(MDC_CORRELATION_ID, correlationId);
        MDC.put(MDC_REQUEST_ID, requestId);
        
        // Add to response headers for downstream services
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        // Log incoming request
        logger.info("Incoming request: {} {} from {} | User-Agent: {}", 
            request.getMethod(), 
            request.getRequestURI(),
            getClientIpAddress(request),
            request.getHeader("User-Agent"));
            
        return true;
    }
    
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                               @NonNull Object handler, @Nullable Exception ex) {
        try {
            Long startTime = (Long) request.getAttribute("startTime");
            long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;
            
            // Log response
            if (ex != null) {
                logger.error("Request completed with exception: {} {} - Status: {} | Duration: {}ms | Exception: {}", 
                    request.getMethod(), 
                    request.getRequestURI(),
                    response.getStatus(),
                    duration,
                    ex.getMessage());
            } else {
                logger.info("Request completed: {} {} - Status: {} | Duration: {}ms", 
                    request.getMethod(), 
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
            }
        } finally {
            // Clean up MDC
            MDC.remove(MDC_CORRELATION_ID);
            MDC.remove(MDC_REQUEST_ID);
        }
    }
    
    /**
     * Extract correlation ID from various possible headers.
     * API gateways use different header names.
     */
    private String extractCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null) {
            correlationId = request.getHeader(TRACE_ID_HEADER);
        }
        if (correlationId == null) {
            correlationId = request.getHeader("X-Amzn-Trace-Id"); // AWS ALB
        }
        if (correlationId == null) {
            correlationId = request.getHeader("X-Cloud-Trace-Context"); // Google Cloud
        }
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        return correlationId;
    }
    
    /**
     * Extract request ID, generate if not present.
     */
    private String extractRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }
    
    /**
     * Get client IP address considering various proxy headers.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
