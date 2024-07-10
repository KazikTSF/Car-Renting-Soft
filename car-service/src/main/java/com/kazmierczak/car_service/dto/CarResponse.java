package com.kazmierczak.car_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarResponse {
    private BigInteger carId;
    private String make;
    private String model;
    private int productionYear;
    private BigDecimal price;
}
