package com.example.accesscontrol.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class for enabling virtual threads in the Spring Boot application.
 * 
 * <p>This configuration provides virtual thread support for async operations,
 * improving I/O performance and concurrency by utilizing Java 21+ virtual threads
 * instead of traditional platform threads.</p>
 * 
 * <p>Virtual threads are lightweight threads that can be created in large numbers
 * without the overhead of platform threads, making them ideal for I/O-bound operations.</p>
 * 
 * @author Backend Architect
 * @since 1.0
 */
@Configuration
@EnableAsync
public class VirtualThreadConfig {

    private static final Logger logger = LoggerFactory.getLogger(VirtualThreadConfig.class);

    /**
     * Creates the default TaskExecutor bean using virtual threads.
     * 
     * <p>This will be used as the default executor for all @Async methods.
     * Virtual threads are lightweight and can be created in large numbers
     * without the overhead of platform threads.</p>
     * 
     * @return Default TaskExecutor using virtual threads
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        logger.info("Configuring default task executor with virtual threads");
        
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
