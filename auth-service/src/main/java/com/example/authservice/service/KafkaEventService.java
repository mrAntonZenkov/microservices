package com.example.authservice.service;

import com.example.authservice.avro.UserCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaEventService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(KafkaEventService.class);

    public KafkaEventService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private static final String USER_CREATED_TOPIC = "user.created";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void sendUserCreatedEvent(Long userId, String email, String role) {
        UserCreated userCreated = UserCreated.newBuilder()
                .setUserId(userId)
                .setEmail(email)
                .setRole(role)
                .setCreatedAt(LocalDateTime.now().format(FORMATTER))
                .build();

        String key = String.valueOf(userId);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(USER_CREATED_TOPIC, key, userCreated);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent user.created event for user ID: {}, offset: {}", userId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send user.created event for user ID: {}", userId, ex);
            }
        });
    }
}
