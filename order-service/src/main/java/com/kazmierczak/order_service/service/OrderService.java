package com.kazmierczak.order_service.service;

import com.kazmierczak.order_service.dto.InventoryResponse;
import com.kazmierczak.order_service.dto.OrderCarDto;
import com.kazmierczak.order_service.dto.OrderRequest;
import com.kazmierczak.order_service.event.OrderPlacedEvent;
import com.kazmierczak.order_service.model.Order;
import com.kazmierczak.order_service.model.OrderCar;
import com.kazmierczak.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        OrderCar orderCar = mapToDto(orderRequest.orderCarDto());
        order.setOrderCar(orderCar);
        InventoryResponse result = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory/" + orderCar.getSkuCode())
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();
        if (result.isInStock()) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
        } else
            throw new IllegalArgumentException("Product not in stock");
    }

    private OrderCar mapToDto(OrderCarDto orderCarDto) {
        OrderCar orderCar = new OrderCar();
        orderCar.setPrice(orderCarDto.getPrice());
        orderCar.setNumberOfDays(orderCarDto.getNumberOfDays());
        orderCar.setSkuCode(orderCarDto.getSkuCode());
        orderCar.setQuantity(orderCarDto.getQuantity());
        return orderCar;
    }
}
