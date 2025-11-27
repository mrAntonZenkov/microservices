package com.example.apigateway.dto;

public record CreateProductDto(
        String title,
        String description,
        String price,
        String category) {}
