package org.example.productservice.controller;

import org.example.productservice.dto.ProductRequestDTO;
import org.example.productservice.dto.ProductResponseDTO;
import org.example.productservice.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductResponseDTO create(@RequestBody ProductRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ProductResponseDTO> list() {
        return service.list();
    }

    @GetMapping("/search")
    public List<ProductResponseDTO> search(@RequestParam String text,
                                           @RequestParam String category,
                                           @RequestParam Double minPrice,
                                           @RequestParam Double maxPrice) {
        return service.search(text, category, minPrice, maxPrice);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductResponseDTO update(@PathVariable String id,
                                     @RequestBody ProductRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductResponseDTO publish(@PathVariable String id) {
        return service.publishProduct(id);
    }

    @PostMapping("/{id}/hide")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ProductResponseDTO hide(@PathVariable String id) {
        return service.hideProduct(id);
    }
}
