package org.example.productservice.repository;

import org.example.productservice.model.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ProductRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Product> search(String text, String category, Double minPrice, Double maxPrice) {
        Query query = new Query();

        if (text != null && !text.isEmpty()) {
            query.addCriteria(Criteria.where("name").regex(text, "i")
                    .orOperator(Criteria.where("description").regex(text, "i")));
        }

        if (category != null && !category.isEmpty()) {
            query.addCriteria(Criteria.where("category").is(category));
        }

        if (minPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice));
        }

        if (maxPrice != null) {
            query.addCriteria(Criteria.where("price").lte(maxPrice));
        }

        return mongoTemplate.find(query, Product.class);
    }
}
