package com.example.template.interceptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tests for RequestResponseLoggingInterceptor.
 * Verifies correlation ID handling and request/response logging.
 */
@ExtendWith(MockitoExtension.class)
class RequestResponseLoggingInterceptorTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private Object handler;
    
    private RequestResponseLoggingInterceptor interceptor;
    
    @BeforeEach
    void setUp() {
        interceptor = new RequestResponseLoggingInterceptor();
        MDC.clear();
    }
    
    @Test
    void testCorrelationIdPropagation() throws Exception {
        // Given
        String expectedCorrelationId = "test-correlation-123";
        when(request.getHeader("X-Correlation-ID")).thenReturn(expectedCorrelationId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Test-Agent");
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);

        // When
        boolean result = interceptor.preHandle(request, response, handler);
        
        // Then
        assertTrue(result);
        verify(response).setHeader("X-Correlation-ID", expectedCorrelationId);
        verify(response).setHeader(eq("X-Request-ID"), anyString());
        
        // Verify MDC contains correlation ID
        assertEquals(expectedCorrelationId, MDC.get("correlationId"));
        assertNotNull(MDC.get("requestId"));
    }
    
    @Test
    void testCorrelationIdGeneration() throws Exception {
        // Given - no correlation ID in request
        when(request.getHeader("X-Correlation-ID")).thenReturn(null);
        when(request.getHeader("X-Trace-ID")).thenReturn(null);
        when(request.getHeader("X-Amzn-Trace-Id")).thenReturn(null);
        when(request.getHeader("X-Cloud-Trace-Context")).thenReturn(null);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/create");
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(request.getHeader("User-Agent")).thenReturn("Test-Client");
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);

        // When
        boolean result = interceptor.preHandle(request, response, handler);
        
        // Then
        assertTrue(result);
        verify(response).setHeader(eq("X-Correlation-ID"), anyString());
        verify(response).setHeader(eq("X-Request-ID"), anyString());
        
        // Verify MDC contains generated correlation ID
        assertNotNull(MDC.get("correlationId"));
        assertNotNull(MDC.get("requestId"));
    }
    
    @Test
    void testXForwardedForHandling() throws Exception {
        // Given
        when(request.getHeader("X-Correlation-ID")).thenReturn("test-123");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.195, 70.41.3.18, 150.172.238.178");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/forwarded");
        when(request.getHeader("User-Agent")).thenReturn("Proxy-Agent");
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        
        // When
        boolean result = interceptor.preHandle(request, response, handler);
        
        // Then
        assertTrue(result);
        // The logging should extract the first IP from X-Forwarded-For
        verify(request).getHeader("X-Forwarded-For");
    }
    
    @Test
    void testAfterCompletionWithoutException() throws Exception {
        // Given
        long startTime = System.currentTimeMillis();
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/success");
        when(response.getStatus()).thenReturn(200);
        
        // When
        interceptor.afterCompletion(request, response, handler, null);
        
        // Then
        verify(request).getAttribute("startTime");
        verify(response).getStatus();
        // MDC should be cleaned up
        assertNull(MDC.get("correlationId"));
        assertNull(MDC.get("requestId"));
    }
    
    @Test
    void testAfterCompletionWithException() throws Exception {
        // Given
        long startTime = System.currentTimeMillis();
        Exception testException = new RuntimeException("Test error");
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/error");
        when(response.getStatus()).thenReturn(500);
        
        // When
        interceptor.afterCompletion(request, response, handler, testException);
        
        // Then
        verify(request).getAttribute("startTime");
        verify(response).getStatus();
        // MDC should be cleaned up even with exception
        assertNull(MDC.get("correlationId"));
        assertNull(MDC.get("requestId"));
    }
}
