package com.example.accesscontrol.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for OpenAPI documentation configuration.
 * Verifies that API documentation is properly generated and accessible.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testOpenApiDocsEndpoint() throws Exception {
        mockMvc.perform(get("/api-docs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Spring Service Template API"))
                .andExpect(jsonPath("$.info.version").value("2.0"))
                .andExpect(jsonPath("$.info.description").exists())
                .andExpect(jsonPath("$.components.securitySchemes").exists())
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication']").exists())
                .andExpect(jsonPath("$.components.securitySchemes['API Key']").exists());
    }

    @Test
    void testSwaggerUiAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound()) // Redirects to swagger-ui/index.html
                .andExpect(header().string("Location", "/swagger-ui/index.html"));
    }

    @Test
    void testApiDocsContainsVersionedEndpoints() throws Exception {
        mockMvc.perform(get("/api-docs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/status']").exists())
                .andExpect(jsonPath("$.paths['/api/v2/status']").exists());
    }

    @Test
    void testSecuritySchemasInDocumentation() throws Exception {
        mockMvc.perform(get("/api-docs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].scheme").value("bearer"))
                .andExpect(jsonPath("$.components.securitySchemes['Bearer Authentication'].bearerFormat").value("JWT"))
                .andExpect(jsonPath("$.components.securitySchemes['API Key'].type").value("apiKey"));
    }
}
