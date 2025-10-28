package org.example.productservice.service;


import com.example.product.grpc.EmptyResponse;
import com.example.product.grpc.ProductIdRequest;
import com.example.product.grpc.ProductRequest;
import com.example.product.grpc.ProductResponse;
import com.example.product.grpc.ProductServiceGrpc;
import com.example.product.grpc.SearchProductsRequest;
import com.example.product.grpc.SearchProductsResponse;
import com.example.product.grpc.UpdateProductRequest;
import io.grpc.stub.StreamObserver;
import org.example.productservice.dto.ProductRequestDTO;
import org.example.productservice.dto.ProductResponseDTO;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;


@GrpcService
public class GrpcProductService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService service;

    public GrpcProductService(ProductService service) {
        this.service = service;
    }

    @Override
    public void createProduct(ProductRequest request,
                              StreamObserver<ProductResponse> responseObserver) {
        ProductRequestDTO dto = new ProductRequestDTO(request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory());
        ProductResponseDTO responseDTO = service.create(dto);

        responseObserver.onNext(toProto(responseDTO));
        responseObserver.onCompleted();
    }

    @Override
    public void getProductById(ProductIdRequest request,
                               StreamObserver<ProductResponse> responseObserver) {
        ProductResponseDTO responseDTO = service.getById(request.getId());
        responseObserver.onNext(toProto(responseDTO));
        responseObserver.onCompleted();
    }

    @Override
    public void updateProduct(UpdateProductRequest request,
                              StreamObserver<ProductResponse> responseObserver) {
        ProductRequestDTO dto = new ProductRequestDTO(request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory());
        ProductResponseDTO responseDTO = service.update(request.getId(), dto);
        responseObserver.onNext(toProto(responseDTO));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(ProductIdRequest request,
                              StreamObserver<EmptyResponse> responseObserver) {
        service.delete(request.getId());
        responseObserver.onNext(EmptyResponse.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void publishProduct(ProductIdRequest request,
                               StreamObserver<ProductResponse> responseObserver) {
        ProductResponseDTO responseDTO = service.publishProduct(request.getId());
        responseObserver.onNext(toProto(responseDTO));
        responseObserver.onCompleted();
    }

    @Override
    public void hideProduct(ProductIdRequest request,
                            StreamObserver<ProductResponse> responseObserver) {
        ProductResponseDTO responseDTO = service.hideProduct(request.getId());
        responseObserver.onNext(toProto(responseDTO));
        responseObserver.onCompleted();
    }

    @Override
    public void searchProducts(SearchProductsRequest request,
                               StreamObserver<SearchProductsResponse> responseObserver) {
        List<ProductResponseDTO> results = service.search(request.getText(),
                request.getCategory(),
                request.getMinPrice() != 0.0 ? request.getMinPrice() : null,
                request.getMaxPrice() != 0.0 ? request.getMaxPrice() : null);

        SearchProductsResponse.Builder builder = SearchProductsResponse.newBuilder();
        for (ProductResponseDTO dto : results) {
            builder.addProducts(toProto(dto));
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private ProductResponse toProto(ProductResponseDTO dto) {
        return ProductResponse.newBuilder()
                .setId(dto.getId())
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setPrice(dto.getPrice())
                .setCategory(dto.getCategory())
                .setPublished(dto.isPublished())
                .build();
    }
}
