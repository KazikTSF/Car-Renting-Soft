package com.kazmierczak.car_service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.math.BigInteger;

@Builder
public record CarResponse(BigInteger carId, String make, String model, int productionYear, BigDecimal price) {
}
