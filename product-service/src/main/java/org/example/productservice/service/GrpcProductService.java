package org.example.productservice.service;


import com.example.product.grpc.CreateProductRequest;
import com.example.product.grpc.DeleteProductRequest;
import com.example.product.grpc.EmptyResponse;
import com.example.product.grpc.GetProductByIdRequest;
import com.example.product.grpc.HideProductRequest;
import com.example.product.grpc.ProductResponse;
import com.example.product.grpc.ProductServiceGrpc;
import com.example.product.grpc.PublishProductRequest;
import com.example.product.grpc.SearchProductsRequest;
import com.example.product.grpc.SearchProductsResponse;
import com.example.product.grpc.UpdateProductRequest;
import io.grpc.stub.StreamObserver;
import org.example.productservice.model.Product;
import org.springframework.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@GrpcService
public class GrpcProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    public GrpcProductService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product created = productService.create(request.getTitle(),
                request.getDescription(),
                new BigDecimal(request.getPrice()),
                request.getCategory());
        responseObserver.onNext(toGrpcProduct(created));
        responseObserver.onCompleted();
    }


    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Optional<Product> updated = productService.update(request.getId(),
                request.getTitle(),
                request.getDescription(),
                new BigDecimal(request.getPrice()),
                request.getCategory());
        handleOptionalProduct(updated, request.getId(), responseObserver);
    }


    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<EmptyResponse> responseObserver) {
        boolean success = productService.delete(request.getId());
        if (success) {
            responseObserver.onNext(EmptyResponse.newBuilder().build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Product with id " + request.getId() + " not found")
                    .asRuntimeException());
        }
    }


    @Override
    public void publishProduct(PublishProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Optional<Product> updated = productService.publishProduct(request.getId());
        handleOptionalProduct(updated, request.getId(), responseObserver);
    }


    @Override
    public void hideProduct(HideProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Optional<Product> updated = productService.hideProduct(request.getId());
        handleOptionalProduct(updated, request.getId(), responseObserver);
    }


    @Override
    public void getProductById(GetProductByIdRequest request, StreamObserver<ProductResponse> responseObserver) {
        Optional<Product> product = productService.getById(request.getId());
        handleOptionalProduct(product, request.getId(), responseObserver);
    }


    @Override
    public void searchProducts(SearchProductsRequest request, StreamObserver<SearchProductsResponse> responseObserver) {
        BigDecimal minPrice = request.getMinPrice().isEmpty() ? null : new BigDecimal(request.getMinPrice());
        BigDecimal maxPrice = request.getMaxPrice().isEmpty() ? null : new BigDecimal(request.getMaxPrice());


        List<Product> products = productService.searchProducts(request.getText(),
                request.getCategory().isEmpty() ? null : request.getCategory(),
                minPrice,
                maxPrice,
                request.getOnlyPublished());

        SearchProductsResponse response = SearchProductsResponse.newBuilder()
                .addAllProducts(products.stream().map(this::toGrpcProduct).collect(Collectors.toList()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    private void handleOptionalProduct(Optional<Product> optionalProduct, String productId, StreamObserver<ProductResponse> responseObserver) {
        optionalProduct.ifPresentOrElse(
                product -> {
                    responseObserver.onNext(toGrpcProduct(product));
                    responseObserver.onCompleted();
                },
                () -> responseObserver.onError(
                        io.grpc.Status.NOT_FOUND
                                .withDescription("Product with id " + productId + " not found")
                                .asRuntimeException()));
    }


    private ProductResponse toGrpcProduct(Product product) {
        return ProductResponse.newBuilder()
                .setId(product.getId())
                .setTitle(product.getTitle())
                .setDescription(product.getDescription())
                .setPrice(product.getPrice().toString())
                .setCategory(product.getCategory())
                .setPublished(product.isPublished())
                .build();
    }
}

