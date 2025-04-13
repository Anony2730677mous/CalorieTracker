package org.example.service;

import org.example.dto.DishResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "dish-service", url = "http://localhost:8082/dishes")
public interface DishServiceClient {

    @GetMapping("/{id}")
    DishResponseDTO getDishById(@PathVariable("id") Long id);
}