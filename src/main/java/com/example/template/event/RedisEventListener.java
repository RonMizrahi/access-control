package com.example.template.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka-redis")
public class RedisEventListener implements MessageListener {
    private static final Logger log = LoggerFactory.getLogger(RedisEventListener.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received Redis message: {}", message.toString());
    }
}
