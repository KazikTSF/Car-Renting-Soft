package com.kazmierczak.order_service.service;

import com.kazmierczak.order_service.dto.OrderCarDto;
import com.kazmierczak.order_service.dto.OrderRequest;
import com.kazmierczak.order_service.model.Order;
import com.kazmierczak.order_service.model.OrderCar;
import com.kazmierczak.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        OrderCar orderCar = mapToDto(orderRequest.getOrderCarDto());
        order.setOrderCar(orderCar);
        orderRepository.save(order);
    }

    private OrderCar mapToDto(OrderCarDto orderCarDto) {
        OrderCar orderCar = new OrderCar();
        orderCar.setPrice(orderCarDto.getPrice());
        orderCar.setNumberOfDays(orderCarDto.getNumberOfDays());
        orderCar.setSkuCode(orderCarDto.getSkuCode());
        return orderCar;
    }
}
