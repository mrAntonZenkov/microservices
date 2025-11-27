package com.example.apigateway.dto;

public record UpdateProductDto(
        String id,
        String title,
        String description,
        String price,
        String category) {}
