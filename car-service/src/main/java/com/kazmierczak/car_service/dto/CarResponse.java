package com.kazmierczak.car_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarResponse {
    private int id;
    private String make;
    private String model;
    private int productionYear;
    private BigDecimal price;
}
