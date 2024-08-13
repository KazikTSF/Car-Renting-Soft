package com.kazmierczak.order_service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.kazmierczak.order_service.dto.InventoryResponse;
import com.kazmierczak.order_service.dto.OrderCarDto;
import com.kazmierczak.order_service.dto.OrderRequest;
import com.kazmierczak.order_service.model.Order;
import com.kazmierczak.order_service.repository.OrderRepository;
import com.kazmierczak.order_service.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.cloud.discovery.client.simple.instances.inventory-service[0].uri=http://localhost:8082"})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@EmbeddedKafka
@WireMockTest
class OrderServiceApplicationTests {
    static MySQLContainer mysql = new MySQLContainer("mysql:latest")
            .withDatabaseName("order-service")
            .withUsername("root")
            .withPassword("root");

    static {
        mysql.start();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mysql::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mysql::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mysql::getPassword);
    }

    @RegisterExtension
    static WireMockExtension wm = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8082))
            .build();

    @AfterEach
    void cleanup() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldPlaceOrder() {
        InventoryResponse inventoryResponse = new InventoryResponse("Corolla2022", true);
        wm.stubFor(get(urlEqualTo("/api/inventory/Corolla2022")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withJsonBody(objectMapper.valueToTree(inventoryResponse))));

        orderService.placeOrder(getCreatedOrder());

        assertEquals(1, orderRepository.findAll().size());
        final Order createdOrder = orderRepository.findAll().get(0);
        assertEquals(1L, createdOrder.getOrderCar().getId());
        assertEquals(14, createdOrder.getOrderCar().getNumberOfDays());
        assertEquals("Corolla2022", createdOrder.getOrderCar().getSkuCode());
        assertEquals(1, createdOrder.getOrderCar().getQuantity());
        assertEquals(BigDecimal.valueOf(150).setScale(0, RoundingMode.HALF_UP),
                createdOrder.getOrderCar().getPrice().setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    void shouldNotPlaceOrder() throws Exception {
        InventoryResponse inventoryResponse = new InventoryResponse("Corolla2022", false);
        wm.stubFor(get(urlPathMatching("/api/inventory/Corolla2022")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withJsonBody(objectMapper.valueToTree(inventoryResponse))));
        String orderRequestString = objectMapper.writeValueAsString(getCreatedOrder());
        assertThatThrownBy(() -> mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestString))).hasMessageContaining("java.lang.IllegalArgumentException: Product not in stock");

    }

    private OrderRequest getCreatedOrder() {
        OrderCarDto orderCarDto = OrderCarDto.builder()
                .id(1L)
                .numberOfDays(14)
                .skuCode("Corolla2022")
                .price(BigDecimal.valueOf(150))
                .quantity(1)
                .build();
        return new OrderRequest(orderCarDto);
    }
}
