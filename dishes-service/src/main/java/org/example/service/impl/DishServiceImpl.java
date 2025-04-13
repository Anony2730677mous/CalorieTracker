package org.example.service.impl;

import org.example.entity.Dish;
import org.example.exception.DishNotFoundException;
import org.example.repository.DishRepository;
import org.example.service.DishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    public DishServiceImpl(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    @Override
    public Optional<Dish> getDishById(Long id) {
        return dishRepository.findById(id);
    }

    @Override
    public Dish getDishByIdOrThrow(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException("Блюдо с ID " + id + " не найдено"));
    }

    @Override
    @Transactional
    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }

    @Override
    @Transactional
    public Dish updateDish(Long id, Dish dish) {
        Dish existingDish = getDishByIdOrThrow(id);
        existingDish.setName(dish.getName());
        existingDish.setCaloriesPerPortion(dish.getCaloriesPerPortion());
        existingDish.setProteinsPer100g(dish.getProteinsPer100g());
        existingDish.setFatsPer100g(dish.getFatsPer100g());
        existingDish.setCarbsPer100g(dish.getCarbsPer100g());
        return dishRepository.save(existingDish);
    }

    @Override
    @Transactional
    public void deleteDish(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new DishNotFoundException("Блюдо с ID " + id + " не найдено");
        }
        dishRepository.deleteById(id);
    }
}
