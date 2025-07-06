package com.example.template.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisCacheServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @InjectMocks
    private RedisCacheService redisCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void set_shouldStoreValue() {
        String key = "testKey";
        String value = "testValue";
        redisCacheService.set(key, value);
        verify(valueOperations).set(key, value);
    }

    @Test
    void get_shouldRetrieveValue() {
        String key = "testKey";
        String value = "testValue";
        when(valueOperations.get(key)).thenReturn(value);
        Object result = redisCacheService.get(key);
        assertEquals(value, result);
    }

    @Test
    void delete_shouldRemoveKey() {
        String key = "testKey";
        redisCacheService.delete(key);
        verify(redisTemplate).delete(key);
    }
}
