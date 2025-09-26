package com.example.authservice.service;

import com.example.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "user.created";

    public void publishUserCreated(User user) {
        kafkaTemplate.send(TOPIC, user.getId().toString(), user.getEmail());
    }
}
