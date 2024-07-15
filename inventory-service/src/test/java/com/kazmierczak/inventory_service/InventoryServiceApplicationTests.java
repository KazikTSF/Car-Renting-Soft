package com.kazmierczak.inventory_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kazmierczak.inventory_service.model.Inventory;
import com.kazmierczak.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

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
	private InventoryRepository inventoryRepository;
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", mysql::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", mysql::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mysql::getPassword);
	}
	@AfterEach
	public void cleanup() {
		inventoryRepository.deleteAll();
	}
	@Test
	void isInStock() throws Exception {
		Inventory inv = Inventory.builder()
				.skuCode("Corolla2022")
				.quantity(1)
				.build();
		inventoryRepository.save(inv);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/Corolla2022"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.inStock").value(true))
				.andExpect(MockMvcResultMatchers.jsonPath("$.skuCode").value("Corolla2022"));
	}
	@Test
	void isNotInStock() throws Exception {
		Inventory inv = Inventory.builder()
				.skuCode("Yaris2023")
				.quantity(1)
				.build();
		inventoryRepository.save(inv);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/Corolla2022"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.inStock").value(false));
	}
	@Test
	void quantityIsZero() throws Exception {
		Inventory inv = Inventory.builder()
				.skuCode("Corolla2022")
				.quantity(0)
				.build();
		inventoryRepository.save(inv);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/Corolla2022"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.inStock").value(false));
	}
}
