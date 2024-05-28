package com.kazmierczak.order_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kazmierczak.order_service.dto.OrderCarDto;
import com.kazmierczak.order_service.dto.OrderRequest;
import com.kazmierczak.order_service.model.Order;
import com.kazmierczak.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderServiceApplicationTests {
	static MySQLContainer mysql = new MySQLContainer("mysql:latest")
			.withDatabaseName("order-service")
			.withUsername("root")
			.withPassword("root");
	static {
		mysql.start();
	}
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private OrderRepository orderRepository;
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", mysql::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mysql::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mysql::getPassword);

	}
	@Test
	void shouldCreateOrder() throws Exception {
		String orderRequestString = objectMapper.writeValueAsString(getCreatedOrder());
		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, orderRepository.findAll().size());
		final Order createdOrder = orderRepository.findAll().get(0);
		Assertions.assertEquals(1L, createdOrder.getOrderCar().getId());
		Assertions.assertEquals(14, createdOrder.getOrderCar().getNumberOfDays());
		Assertions.assertEquals("Corolla", createdOrder.getOrderCar().getSkuCode());
		Assertions.assertEquals(BigDecimal.valueOf(150).setScale(0, RoundingMode.HALF_UP),
				createdOrder.getOrderCar().getPrice().setScale(0, RoundingMode.HALF_UP));
	}

	private OrderRequest getCreatedOrder() {
		OrderCarDto orderCarDto = OrderCarDto.builder()
				.id(1L)
				.numberOfDays(14)
				.skuCode("Corolla")
				.price(BigDecimal.valueOf(150))
				.build();
		return new OrderRequest(orderCarDto);
	}
}
