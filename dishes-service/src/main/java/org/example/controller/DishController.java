package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.DishDTO;
import org.example.entity.Dish;
import org.example.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<DishDTO> getAllDishes() {
        return dishService.getAllDishes().stream()
                .map(DishDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDTO> getDishById(@PathVariable Long id) {
        Dish dish = dishService.getDishByIdOrThrow(id);
        return ResponseEntity.ok(DishDTO.fromEntity(dish));
    }

    @PostMapping
    public ResponseEntity<DishDTO> createDish(@Valid @RequestBody DishDTO dishDTO) {
        Dish dish = dishDTO.toEntity();
        Dish savedDish = dishService.createDish(dish);
        return ResponseEntity.ok(DishDTO.fromEntity(savedDish));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishDTO> updateDish(@PathVariable Long id, @Valid @RequestBody DishDTO dishDTO) {
        Dish dish = dishDTO.toEntity();
        Dish updatedDish = dishService.updateDish(id, dish);
        return ResponseEntity.ok(DishDTO.fromEntity(updatedDish));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }
}
