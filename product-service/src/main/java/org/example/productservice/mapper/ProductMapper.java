package org.example.productservice.mapper;

import org.example.productservice.dto.ProductDTO;
import org.example.productservice.dto.ProductRequestDTO;
import org.example.productservice.dto.ProductResponseDTO;
import org.example.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductDTO toDTO(Product product);
    Product toEntity(ProductRequestDTO dto);
    ProductResponseDTO toResponse(ProductDTO dto);
    ProductResponseDTO toResponseWithPublished(ProductDTO dto, boolean published);
}
