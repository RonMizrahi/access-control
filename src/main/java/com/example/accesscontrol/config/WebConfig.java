package com.example.accesscontrol.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.accesscontrol.interceptor.SubscriptionRateLimitInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private SubscriptionRateLimitInterceptor subscriptionRateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(subscriptionRateLimitInterceptor)
                .addPathPatterns("/**");
                //.excludePathPatterns("/auth/**");
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
