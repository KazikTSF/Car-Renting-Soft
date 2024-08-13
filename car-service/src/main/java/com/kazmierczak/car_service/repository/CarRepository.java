package com.kazmierczak.car_service.repository;

import com.kazmierczak.car_service.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

public interface CarRepository extends MongoRepository<Car, BigInteger> {
}
