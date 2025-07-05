package com.example.accesscontrol.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.accesscontrol.config.JwtConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Import(JwtConfig.class)
class AuthControllerIntegrationTest {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private JwtConfig jwtConfig;

        @Test
        @DisplayName("Login with valid credentials returns JWT token")
        void login_withValidCredentials_returnsToken() throws Exception {
                String json = "{" +
                                "\"username\": \"test-admin\"," +
                                "\"password\": \"password\"}";
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("Login with invalid credentials returns 401")
        void login_withInvalidCredentials_returns401() throws Exception {
                String json = "{" +
                                "\"username\": \"test-admin\"," +
                                "\"password\": \"wrongpass\"}";
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value("Invalid credentials"));
        }

        @Test
        @DisplayName("Access admin-roles endpoint with admin token succeeds")
        void adminRoles_withAdminToken_succeeds() throws Exception {
                String token = jwtConfig.obtainToken("test-admin", "password");
                mockMvc.perform(get("/auth/admin-roles")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("test-admin"))
                                .andExpect(jsonPath("$.roles").isArray());
        }

        @Test
        @DisplayName("Access admin-roles endpoint with user token is forbidden")
        void adminRoles_withUserToken_forbidden() throws Exception {
                String token = jwtConfig.obtainToken("test-user", "password");
                mockMvc.perform(get("/auth/admin-roles")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Access user-roles endpoint with user token succeeds")
        void userRoles_withUserToken_succeeds() throws Exception {
                String token = jwtConfig.obtainToken("test-user", "password");
                mockMvc.perform(get("/auth/user-roles")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("test-user"))
                                .andExpect(jsonPath("$.roles").isArray());
        }

        @Test
        @DisplayName("Access user-roles endpoint with admin token is forbidden")
        void userRoles_withAdminToken_forbidden() throws Exception {
                String token = jwtConfig.obtainToken("test-admin", "password");
                mockMvc.perform(get("/auth/user-roles")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isForbidden());
        }

}
