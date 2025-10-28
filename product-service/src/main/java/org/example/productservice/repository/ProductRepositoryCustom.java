package org.example.productservice.repository;

import org.example.productservice.model.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> search(String text, String category, Double minPrice, Double maxPrice);
}
