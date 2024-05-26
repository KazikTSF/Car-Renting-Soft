package com.kazmierczak.car_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kazmierczak.car_service.dto.CarRequest;
import com.kazmierczak.car_service.dto.CarResponse;
import com.kazmierczak.car_service.model.Car;
import com.kazmierczak.car_service.repository.CarRepository;
import com.kazmierczak.car_service.service.CarService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CarServiceApplicationTests {

	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
	static {
		mongoDBContainer.start();
	}
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private CarRepository carRepository;
	@MockBean
	CarService carService;
	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}
	@Test
	void shouldCreateCar() throws Exception {
		String carRequestString = objectMapper.writeValueAsString(getCarRequest());
		mockMvc.perform(MockMvcRequestBuilders.post("/api/car")
				.contentType(MediaType.APPLICATION_JSON)
				.content(carRequestString))
				.andExpect(status().isCreated());
        Assertions.assertEquals(1, carRepository.findAll().size());
		final Car createdCar = carRepository.findAll().get(0);
		Assertions.assertEquals("Toyota", createdCar.getMake());
        Assertions.assertEquals("Corolla", createdCar.getModel());
        Assertions.assertEquals(2022, createdCar.getProductionYear());
        Assertions.assertEquals(BigDecimal.valueOf(169), createdCar.getPrice());
	}
	@Test
	void shouldShowAllCars() throws Exception {
		CarResponse car1 = CarResponse.builder()
				.make("Toyota")
				.model("Corolla")
				.productionYear(2022)
				.price(BigDecimal.valueOf(169))
				.build();
		CarResponse car2 = CarResponse.builder()
				.make("Toyota")
				.model("Yaris")
				.productionYear(2023)
				.price(BigDecimal.valueOf(129))
				.build();
		Mockito.when(carService.getAllCars()).thenReturn(Arrays.asList(car1, car2));
		mockMvc.perform(MockMvcRequestBuilders.get("/api/car"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].make").value("Toyota"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].model").value("Corolla"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].productionYear").value(2022))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].price").value(BigDecimal.valueOf(169)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].make").value("Toyota"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].model").value("Yaris"))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].productionYear").value(2023))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].price").value(BigDecimal.valueOf(129)));
	}

	private CarRequest getCarRequest() {
		return CarRequest.builder()
				.make("Toyota")
				.model("Corolla")
				.productionYear(2022)
				.price(BigDecimal.valueOf(169))
				.build();
	}

}
