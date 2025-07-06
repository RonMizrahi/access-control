package com.example.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestConfiguration
public class JwtConfig {

    @Autowired
    private MockMvc mockMvc;

    public String obtainToken(String username, String password) throws Exception {
        String json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        String response = result.andReturn().getResponse().getContentAsString();
        int start = response.indexOf(":\"") + 2;
        int end = response.indexOf("\"}");
        return response.substring(start, end);
    }
}
