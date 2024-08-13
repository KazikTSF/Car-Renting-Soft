package com.kazmierczak.inventory_service.service;

import com.kazmierczak.inventory_service.dto.InventoryResponse;
import com.kazmierczak.inventory_service.model.Inventory;
import com.kazmierczak.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public InventoryResponse isInStock(String skuCode) {
        if (inventoryRepository.findBySkuCode(skuCode).isEmpty())
            return InventoryResponse.builder()
                    .isInStock(false)
                    .build();
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode).get();
        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build();
    }
}
