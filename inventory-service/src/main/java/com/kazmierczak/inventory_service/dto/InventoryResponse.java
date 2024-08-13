package com.kazmierczak.inventory_service.dto;

import lombok.Builder;

@Builder
public record InventoryResponse(String skuCode, boolean isInStock) {
}
