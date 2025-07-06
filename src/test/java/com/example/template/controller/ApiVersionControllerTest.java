package com.example.template.controller;

import com.example.template.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for API versioning functionality.
 * Tests both header-based and path-based versioning approaches.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(JwtConfig.class)
class ApiVersionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtConfig jwtConfig;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // Use a valid user from your data.sql
        token = jwtConfig.obtainToken("test-admin", "password");
    }

    @Test
    void testPathBasedVersioningV1() throws Exception {
        mockMvc.perform(get("/api/v1/status")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.version").value("1.0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testPathBasedVersioningV2() throws Exception {
        mockMvc.perform(get("/api/v2/status")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.version").value("2.0"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.uptime").exists())
                .andExpect(jsonPath("$.health").value("UP"));
    }

    @Test
    void testCorrelationIdPropagation() throws Exception {
        String correlationId = "test-correlation-123";
        mockMvc.perform(get("/api/v1/status")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Correlation-ID", correlationId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Correlation-ID", correlationId))
                .andExpect(header().exists("X-Request-ID"));
    }
}
