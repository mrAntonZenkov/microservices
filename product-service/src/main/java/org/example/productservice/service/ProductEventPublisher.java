package org.example.productservice.service;

import org.example.productservice.model.Product;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_CREATED = "product.created";
    private static final String TOPIC_UPDATED = "product.updated";
    private static final String TOPIC_PUBLISHED = "product.published";
    private static final String TOPIC_HIDDEN = "product.hidden";

    public ProductEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreated(Product product) {
        kafkaTemplate.send(TOPIC_CREATED, product.getId(), product);
    }

    public void publishUpdated(Product product) {
        kafkaTemplate.send(TOPIC_UPDATED, product.getId(), product);
    }

    public void publishPublished(Product product) {
        kafkaTemplate.send(TOPIC_PUBLISHED, product.getId(), product);
    }

    public void publishHidden(Product product) {
        kafkaTemplate.send(TOPIC_HIDDEN, product.getId(), product);
    }
}
