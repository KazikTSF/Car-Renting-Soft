package com.kazmierczak.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCarDto {
    @Id
    private Long id;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
    private Integer numberOfDays;
}
