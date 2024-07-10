package com.kazmierczak.car_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.BigInteger;

@Document(collection = "car")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Car {
    @Id
    private BigInteger carId;
    private String make;
    private String model;
    private int productionYear;
    private BigDecimal price;
}
