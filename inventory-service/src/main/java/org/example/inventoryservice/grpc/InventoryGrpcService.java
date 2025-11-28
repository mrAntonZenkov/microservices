package org.example.inventoryservice.grpc;

import com.example.inventory.grpc.InventoryCheckRequest;
import com.example.inventory.grpc.InventoryCheckResponse;
import com.example.inventory.grpc.InventoryServiceGrpc;
import io.grpc.stub.StreamObserver;


import org.example.inventoryservice.entity.Inventory;
import org.example.inventoryservice.repository.InventoryRepository;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final InventoryRepository inventoryRepository;

    public InventoryGrpcService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void checkAndReserve(InventoryCheckRequest request,
                                StreamObserver<InventoryCheckResponse> responseObserver) {

        boolean allAvailable = true;

        // Проверяем и резервируем
        for (var item : request.getItemsList()) {
            Optional<Inventory> invOpt = inventoryRepository.findByProductId(item.getProductId());
            if (invOpt.isEmpty() || invOpt.get().getQuantity() < item.getQuantity()) {
                allAvailable = false;
                break;
            }
        }

        InventoryCheckResponse.Builder responseBuilder = InventoryCheckResponse.newBuilder();
        responseBuilder.setSuccess(allAvailable);

        // Если всё ок, уменьшаем количество на складе
        if (allAvailable) {
            for (var item : request.getItemsList()) {
                Inventory inv = inventoryRepository.findByProductId(item.getProductId()).get();
                inv.setQuantity(inv.getQuantity() - item.getQuantity());
                inventoryRepository.save(inv);
            }
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
