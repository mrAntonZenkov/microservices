package org.example.orderservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    private final KafkaTopics kafkaTopics;

    public KafkaConfig(KafkaTopics kafkaTopics) {
        this.kafkaTopics = kafkaTopics;
    }

    @Bean
    public org.apache.kafka.clients.admin.NewTopic orderCreatedTopic() {
        return TopicBuilder.name(kafkaTopics.getOrderCreated())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public org.apache.kafka.clients.admin.NewTopic orderStatusChangedTopic() {
        return TopicBuilder.name(kafkaTopics.getOrderStatusChanged())
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public org.apache.kafka.clients.admin.NewTopic inventoryReservedTopic() {
        return TopicBuilder.name(kafkaTopics.getInventoryReserved())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
