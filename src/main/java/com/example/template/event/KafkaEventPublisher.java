package com.example.template.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Component
@Profile("kafka-redis")
@Slf4j
public class KafkaEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;

    public KafkaEventPublisher(
        KafkaTemplate<String, String> kafkaTemplate,
        @Value("${app.kafka.topic:topic2}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMessage(String key, String value) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, value);
        future.thenAccept(result -> {
            log.info("Produced event to topic {}: key = {} value = {}", topic, key, value);
        }).exceptionally(ex -> {
            log.error("Failed to send message to Kafka", ex);
            return null;
        });
    }
}
