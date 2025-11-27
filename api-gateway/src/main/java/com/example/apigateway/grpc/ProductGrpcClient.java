package com.example.apigateway.grpc;

import com.example.apigateway.dto.CreateProductDto;
import com.example.apigateway.dto.UpdateProductDto;
import com.example.product.grpc.DeleteProductRequest;
import com.example.product.grpc.CreateProductRequest;
import com.example.product.grpc.GetProductByIdRequest;
import com.example.product.grpc.HideProductRequest;
import com.example.product.grpc.ProductResponse;
import com.example.product.grpc.ProductServiceGrpc;
import com.example.product.grpc.PublishProductRequest;
import com.example.product.grpc.SearchProductsRequest;
import com.example.product.grpc.SearchProductsResponse;
import com.example.product.grpc.UpdateProductRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ProductGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub stub;

    public ProductResponse createProduct(CreateProductDto dto) {
        CreateProductRequest request = CreateProductRequest.newBuilder()
                .setTitle(dto.title())
                .setDescription(dto.description())
                .setPrice(dto.price())
                .setCategory(dto.category())
                .build();

        return stub.createProduct(request);
    }

    public ProductResponse getProductById(String id) {
        GetProductByIdRequest request = GetProductByIdRequest.newBuilder()
                .setId(id)
                .build();

        return stub.getProductById(request);
    }

    public ProductResponse updateProduct(UpdateProductDto dto) {
        UpdateProductRequest request = UpdateProductRequest.newBuilder()
                .setId(dto.id())
                .setTitle(dto.title())
                .setDescription(dto.description())
                .setPrice(dto.price())
                .setCategory(dto.category())
                .build();

        return stub.updateProduct(request);
    }

    public void deleteProduct(String id) {
        DeleteProductRequest request = DeleteProductRequest.newBuilder()
                .setId(id)
                .build();

        stub.deleteProduct(request);
    }

    public ProductResponse publishProduct(String id) {
        PublishProductRequest request = PublishProductRequest.newBuilder()
                .setId(id)
                .build();

        return stub.publishProduct(request);
    }

    public ProductResponse hideProduct(String id) {
        HideProductRequest request = HideProductRequest.newBuilder()
                .setId(id)
                .build();

        return stub.hideProduct(request);
    }

    public SearchProductsResponse search(String text, String category, String minPrice, String maxPrice, boolean onlyPublished) {
        SearchProductsRequest request = SearchProductsRequest.newBuilder()
                .setText(text == null ? "" : text)
                .setCategory(category == null ? "" : category)
                .setMinPrice(minPrice == null ? "" : minPrice)
                .setMaxPrice(maxPrice == null ? "" : maxPrice)
                .setOnlyPublished(onlyPublished)
                .build();

        return stub.searchProducts(request);
    }
}
