package com.kazmierczak.car_service.controller;

import com.kazmierczak.car_service.dto.CarRequest;
import com.kazmierczak.car_service.dto.CarResponse;
import com.kazmierczak.car_service.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody CarRequest carRequest) {
        carService.createCar(carRequest);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CarResponse> getAllCars() {
        return carService.getAllCars();
    }
}
