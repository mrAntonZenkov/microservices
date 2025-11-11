package org.example.productservice.service;


import org.apache.avro.Schema;


import org.example.productservice.model.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;
    private final MongoTemplate mongoTemplate;

    public ProductService(ProductRepository productRepository, ProductEventPublisher eventPublisher,
                          MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.mongoTemplate = mongoTemplate;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Product create(String title, String description, BigDecimal price, String category) {
        Product product = new Product();
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setPublished(false);
        eventPublisher.publishCreated(product);
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> update(String id, String title, String description, BigDecimal price, String category) {
        return productRepository.findById(id).map(product -> {
            product.setTitle(title);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            eventPublisher.publishUpdated(product);
            return productRepository.save(product);
        });
    }

    @Transactional
    public boolean delete(String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<Product> publishProduct(String id) {
        return productRepository.findById(id).map(product -> {
            product.setPublished(true);
            eventPublisher.publishPublished(product);
            return productRepository.save(product);
        });
    }

    @Transactional
    public Optional<Product> hideProduct(String id) {
        return productRepository.findById(id).map(product -> {
            product.setPublished(false);
            eventPublisher.publishHidden(product);
            return productRepository.save(product);
        });
    }

    public Optional<Product> getById(String id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String text, String category, BigDecimal minPrice, BigDecimal maxPrice, boolean onlyPublished) {
        Criteria criteria = new Criteria();

        if (text != null && !text.isEmpty()) {
            Criteria textCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(text, "i"),
                    Criteria.where("description").regex(text, "i")
            );
            criteria.andOperator(textCriteria);
        }

        if (category != null && !category.isEmpty()) {
            criteria.and("category").is(category);
        }

        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = new Criteria();
            if (minPrice != null) priceCriteria = priceCriteria.gte(minPrice);
            if (maxPrice != null) priceCriteria = priceCriteria.lte(maxPrice);
            criteria.and("price").is(priceCriteria);
        }

        if (onlyPublished) {
            criteria.and("published").is(true);
        }


        Query query = new Query(criteria);
        return mongoTemplate.find(query, Product.class);
    }
}