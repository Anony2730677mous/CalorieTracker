package service;

import org.example.entity.Dish;
import org.example.exception.DishNotFoundException;
import org.example.repository.DishRepository;
import org.example.service.impl.DishServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DishServiceImplTest {

    @Mock
    private DishRepository dishRepository;

    private DishServiceImpl dishService;

    private Dish dish;

    @BeforeEach
    void setUp() {
        dishService = new DishServiceImpl(dishRepository);
        dish = new Dish();
        ReflectionTestUtils.setField(dish, "id", 1L);
        dish.setName("Test Dish");
        dish.setCaloriesPerPortion(300.0);
        dish.setProteinsPer100g(10.0);
        dish.setFatsPer100g(5.0);
        dish.setCarbsPer100g(50.0);
    }

    @Test
    void getAllDishes_Success() {
        List<Dish> dishes = List.of(dish);
        Mockito.when(dishRepository.findAll()).thenReturn(dishes);
        List<Dish> returnedDishes = dishService.getAllDishes();

        Assertions.assertNotNull(returnedDishes, "Список блюд не должен быть null");
        Assertions.assertEquals(1, returnedDishes.size(), "Ожидается 1 блюдо");
        Assertions.assertEquals(dish.getId(), returnedDishes.get(0).getId(), "ID блюда должен совпадать");
    }

    @Test
    void getDishById_Success() {
        Mockito.when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        Optional<Dish> result = dishService.getDishById(1L);

        Assertions.assertTrue(result.isPresent(), "Блюдо должно присутствовать");
        Assertions.assertEquals("Test Dish", result.get().getName(), "Имя блюда должно совпадать");
    }

    @Test
    void getDishByIdOrThrow_Success() {
        Mockito.when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        Dish foundDish = dishService.getDishByIdOrThrow(1L);

        Assertions.assertNotNull(foundDish, "Найденное блюдо не должно быть null");
        Assertions.assertEquals("Test Dish", foundDish.getName(), "Имя блюда должно совпадать");
    }

    @Test
    void getDishByIdOrThrow_NotFound() {
        Mockito.when(dishRepository.findById(1L)).thenReturn(Optional.empty());
        DishNotFoundException exception = Assertions.assertThrows(
                DishNotFoundException.class,
                () -> dishService.getDishByIdOrThrow(1L)
        );
        Assertions.assertEquals("Блюдо с ID 1 не найдено", exception.getMessage(), "Сообщение об ошибке должно соответствовать");
    }

    @Test
    void createDish_Success() {
        Mockito.when(dishRepository.save(Mockito.any(Dish.class))).thenReturn(dish);
        Dish createdDish = dishService.createDish(dish);

        Assertions.assertNotNull(createdDish, "Созданное блюдо не должно быть null");
        Assertions.assertEquals("Test Dish", createdDish.getName(), "Имя блюда должно совпадать");
        Mockito.verify(dishRepository, Mockito.times(1)).save(dish);
    }

    @Test
    void updateDish_Success() {
        Dish updatedData = new Dish();
        updatedData.setName("Updated Dish");
        updatedData.setCaloriesPerPortion(350.0);
        updatedData.setProteinsPer100g(12.0);
        updatedData.setFatsPer100g(6.0);
        updatedData.setCarbsPer100g(55.0);

        Mockito.when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));
        Mockito.when(dishRepository.save(Mockito.any(Dish.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Dish updatedDish = dishService.updateDish(1L, updatedData);

        Assertions.assertNotNull(updatedDish, "Обновлённое блюдо не должно быть null");
        Assertions.assertEquals("Updated Dish", updatedDish.getName(), "Имя блюда должно обновиться");
        Assertions.assertEquals(350.0, updatedDish.getCaloriesPerPortion(), "Калории должны обновиться");
        Assertions.assertEquals(12.0, updatedDish.getProteinsPer100g(), "Белки должны обновиться");
        Assertions.assertEquals(6.0, updatedDish.getFatsPer100g(), "Жиры должны обновиться");
        Assertions.assertEquals(55.0, updatedDish.getCarbsPer100g(), "Углеводы должны обновиться");

        Mockito.verify(dishRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(dishRepository, Mockito.times(1)).save(updatedDish);
    }

    @Test
    void deleteDish_Success() {
        Mockito.when(dishRepository.existsById(1L)).thenReturn(true);
        dishService.deleteDish(1L);

        Mockito.verify(dishRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteDish_NotFound() {
        Mockito.when(dishRepository.existsById(1L)).thenReturn(false);
        DishNotFoundException exception = Assertions.assertThrows(
                DishNotFoundException.class,
                () -> dishService.deleteDish(1L)
        );
        Assertions.assertEquals("Блюдо с ID 1 не найдено", exception.getMessage(), "Сообщение об ошибке должно соответствовать");
    }
}
