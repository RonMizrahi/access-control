package com.example.accesscontrol.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Executor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration tests for VirtualThreadConfig to verify virtual thread executor configuration.
 * 
 * @author Backend Architect
 * @since 1.0
 */
@SpringBootTest
class VirtualThreadConfigTest {

    @Autowired
    @Qualifier("virtualThreadExecutor")
    private Executor virtualThreadExecutor;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    @Test
    @DisplayName("Virtual thread executor bean should be created and configured properly")
    void virtualThreadExecutor_shouldBeConfigured() {
        // Verify that the virtual thread executor bean is created
        assertNotNull(virtualThreadExecutor, "Virtual thread executor should not be null");
        
        // Verify it's a virtual thread executor by checking its class
        String executorClass = virtualThreadExecutor.getClass().getSimpleName();
        assertTrue(executorClass.contains("VirtualThreadPerTaskExecutor") || 
                  executorClass.contains("ThreadPerTaskExecutor"),
                  "Executor should be a VirtualThreadPerTaskExecutor, but was: " + executorClass);
    }

    @Test
    @DisplayName("Default task executor should use virtual threads")
    void taskExecutor_shouldUseVirtualThreads() {
        // Verify that the default task executor bean is created
        assertNotNull(taskExecutor, "Task executor should not be null");
        
        // Verify it's a virtual thread executor by checking its class
        String executorClass = taskExecutor.getClass().getSimpleName();
        assertTrue(executorClass.contains("VirtualThreadPerTaskExecutor") || 
                  executorClass.contains("ThreadPerTaskExecutor"),
                  "Default task executor should be a VirtualThreadPerTaskExecutor, but was: " + executorClass);
    }
}
