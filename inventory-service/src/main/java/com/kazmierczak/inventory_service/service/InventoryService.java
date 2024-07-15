package com.kazmierczak.inventory_service.service;

import com.kazmierczak.inventory_service.dto.InventoryResponse;
import com.kazmierczak.inventory_service.model.Inventory;
import com.kazmierczak.inventory_service.repository.InventoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public InventoryResponse isInStock(String skuCode) {
        if(inventoryRepository.findBySkuCode(skuCode).isEmpty())
            return InventoryResponse.builder()
                    .isInStock(false)
                    .build();
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode).get();
        return InventoryResponse.builder()
                .SkuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build();
    }
}
