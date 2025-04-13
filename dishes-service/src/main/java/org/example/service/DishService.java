package org.example.service;

import org.example.entity.Dish;

import java.util.List;
import java.util.Optional;

public interface DishService {
    List<Dish> getAllDishes();
    Optional<Dish> getDishById(Long id);
    Dish getDishByIdOrThrow(Long id);
    Dish createDish(Dish dish);
    Dish updateDish(Long id, Dish dish);
    void deleteDish(Long id);
}