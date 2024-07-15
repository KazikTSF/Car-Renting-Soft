package com.kazmierczak.order_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.kazmierczak.order_service.dto.OrderCarDto;
import com.kazmierczak.order_service.dto.OrderRequest;
import com.kazmierczak.order_service.model.Order;
import com.kazmierczak.order_service.repository.OrderRepository;
import org.junit.Rule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
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

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8082));
	private final WireMockServer wm = new WireMockServer(8082);

	@BeforeEach
	void setUp() {
		wm.start();
	}
	@AfterEach
	void cleanup() {
		orderRepository.deleteAll();
		wm.stop();
	}
	@Test
	void shouldPlaceOrder() throws Exception {
		wm.stubFor(get(urlPathMatching("/api/inventory/Corolla2022"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("{\"inStock\":true,\"skuCode\":\"Corolla2022\"}")));
		String orderRequestString = objectMapper.writeValueAsString(getCreatedOrder());
		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
						.contentType(MediaType.APPLICATION_JSON)
						.content(orderRequestString))
				.andExpect(status().isCreated());
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
		wm.stubFor(get(urlPathMatching("/api/inventory/Corolla2022"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("{\"inStock\":false,\"skuCode\":\"Corolla2022\"}")));
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
