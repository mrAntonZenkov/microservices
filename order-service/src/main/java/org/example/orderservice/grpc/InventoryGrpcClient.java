package org.example.orderservice.grpc;

import com.example.inventory.grpc.InventoryCheckRequest;
import com.example.inventory.grpc.InventoryCheckResponse;
import com.example.inventory.grpc.InventoryServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.orderservice.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryGrpcClient {

    @GrpcClient("inventory-service")
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    public boolean reserveItems(Long orderId, List<OrderItem> items) {
        InventoryCheckRequest.Builder requestBuilder = InventoryCheckRequest.newBuilder()
                .setOrderId(orderId);

        for (var i : items) {
            requestBuilder.addItems(
                    InventoryCheckRequest.Item.newBuilder()
                            .setProductId(i.getProductId())
                            .setQuantity(i.getQuantity())
                            .build()
            );
        }

        InventoryCheckResponse response = stub.checkAndReserve(requestBuilder.build());
        return response.getSuccess();
    }
}
