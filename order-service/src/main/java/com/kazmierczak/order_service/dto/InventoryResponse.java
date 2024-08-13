package com.kazmierczak.order_service.dto;

import lombok.Builder;

@Builder
public record InventoryResponse(String SkuCode, boolean isInStock) {
}
