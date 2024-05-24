package com.kazmierczak.car_service.service;

import com.kazmierczak.car_service.dto.CarRequest;
import com.kazmierczak.car_service.dto.CarResponse;
import com.kazmierczak.car_service.model.Car;
import com.kazmierczak.car_service.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final CarRepository carRepository;

    public void createCar(CarRequest carRequest) {
        Car car = Car.builder()
                .make(carRequest.getMake())
                .model(carRequest.getModel())
                .productionYear(carRequest.getProductionYear())
                .price(carRequest.getPrice())
                .build();
        carRepository.save(car);
        log.info("Car {} is saved", car.getId());
    }

    public List<CarResponse> getAllCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream().map(this::mapToCarResponse).toList();
    }

    private CarResponse mapToCarResponse(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .make(car.getMake())
                .model(car.getModel())
                .productionYear(car.getProductionYear())
                .price(car.getPrice())
                .build();
    }
}
