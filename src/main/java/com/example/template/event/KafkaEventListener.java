package com.example.template.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka-redis")
public class KafkaEventListener {
    private static final Logger log = LoggerFactory.getLogger(KafkaEventListener.class);

    @KafkaListener(id = "myConsumer", topics = "${app.kafka.topic:test-topic}", groupId = "${KAFKA_CONSUMER_GROUP:template-group}")
    public void listen(String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key) {
        try {
            log.info("Received Kafka message: topic={}, key={}, value={}", topic, key, message);
            // Add message processing or persistence here if needed
        } catch (Exception ex) {
            log.error("Error processing Kafka message", ex);
            // Optionally: send to DLQ or take recovery action
        }
    }
}
