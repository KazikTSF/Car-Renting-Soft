package com.kazmierczak.car_service.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CarRequest(String make, String model, int productionYear, BigDecimal price) {
}
