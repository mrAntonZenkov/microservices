package org.example.productservice.service;

import org.example.productservice.dto.ProductRequestDTO;
import org.example.productservice.dto.ProductResponseDTO;
import org.example.productservice.exception.ProductKafkaException;
import org.example.productservice.exception.ProductNotFoundException;
import org.example.productservice.mapper.ProductMapper;
import org.example.productservice.model.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String TOPIC = "product.events";

    public ProductService(ProductRepository repository,
                          ProductMapper mapper,
                          KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = repository.save(mapper.toEntity(dto));
        publishEvent("CREATED", product);
        return mapper.toResponse(mapper.toDTO(product));
    }

    @Transactional
    public ProductResponseDTO update(String id, ProductRequestDTO dto) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        repository.save(product);
        publishEvent("UPDATED", product);
        return mapper.toResponse(mapper.toDTO(product));
    }

    @Transactional
    public void delete(String id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        repository.deleteById(id);
        publishEvent("DELETED", product);
    }

    @Transactional
    public ProductResponseDTO publishProduct(String id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setPublished(true);
        repository.save(product);
        publishEvent("PUBLISHED", product); // событие Kafka
        return mapper.toResponse(mapper.toDTO(product));
    }

    @Transactional
    public ProductResponseDTO hideProduct(String id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setPublished(false);
        repository.save(product);
        publishEvent("HIDDEN", product); // событие Kafka
        return mapper.toResponse(mapper.toDTO(product));
    }

    public ProductResponseDTO getById(String id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return mapper.toResponse(mapper.toDTO(product));
    }

    public List<ProductResponseDTO> search(String text, String category, Double minPrice, Double maxPrice) {
        return repository.search(text, category, minPrice, maxPrice).stream()
                .map(mapper::toDTO)
                .map(mapper::toResponse)
                .toList();
    }

    public List<ProductResponseDTO> list() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .map(mapper::toResponse)
                .toList();
    }

    private void publishEvent(String action, Product product) {
        try {
            String payload = String.format("{\"action\":\"%s\",\"id\":\"%s\",\"name\":\"%s\",\"price\":%s}",
                    action, product.getId(), product.getName(), product.getPrice());
            kafkaTemplate.send(TOPIC, product.getId(), payload);
        } catch (Exception e) {
            throw new ProductKafkaException("Failed to publish product event", e);
        }
    }
}