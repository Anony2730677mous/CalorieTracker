package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.JacksonConfig;
import org.example.dto.DishResponseDTO;
import org.example.entity.MealDish;
import org.example.entity.MealLog;
import org.example.exception.MealLogNotFoundException;
import org.example.kafka.KafkaProducerService;
import org.example.repository.MealLogRepository;
import org.example.service.DishServiceClient;
import org.example.service.impl.MealLogServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class MealLogServiceImplTest {

    @Mock
    private MealLogRepository mealLogRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private DishServiceClient dishServiceClient;

    private MealLogServiceImpl mealLogService;

    private MealLog mealLog;


    @BeforeEach
    void setUp() {

        ObjectMapper objectMapper = new JacksonConfig().objectMapper();
        mealLogService = new MealLogServiceImpl(mealLogRepository, kafkaProducerService, dishServiceClient, objectMapper);

        mealLog = new MealLog();
        mealLog.setUserId(7L);
        mealLog.setMealTime(LocalDateTime.of(2025, 4, 6, 12, 0));
        mealLog.setDishes(new ArrayList<>());

        MealDish dish = new MealDish();
        dish.setDishId(1L);
        dish.setQuantity(2);
        mealLog.getDishes().add(dish);
    }

    @Test
    void createMealLog_Success() throws JsonProcessingException {
        Long dishId = 1L;

        DishResponseDTO dishResponse = new DishResponseDTO();
        dishResponse.setCaloriesPerPortion(100.0);

        Mockito.when(dishServiceClient.getDishById(dishId)).thenReturn(dishResponse);
        Mockito.when(mealLogRepository.save(mealLog)).thenReturn(mealLog);

        ObjectMapper mapper = new JacksonConfig().objectMapper();
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", mealLog.getUserId());
        eventData.put("reportDate", mealLog.getMealTime());
        List<Map<String, Object>> dishesData = new ArrayList<>();
        for (MealDish dish : mealLog.getDishes()) {
            Map<String, Object> dishData = new HashMap<>();
            dishData.put("dishId", dish.getDishId());
            dishData.put("quantity", dish.getQuantity());
            dishData.put("calories", (int) (dishResponse.getCaloriesPerPortion() * dish.getQuantity())); // Приводим к int
            dishesData.add(dishData);
        }
        eventData.put("dishes", dishesData);

        String jsonMessage = mapper.writeValueAsString(eventData);

        MealLog savedMealLog = mealLogService.createMealLog(mealLog);

        Assertions.assertNotNull(savedMealLog);
        Assertions.assertEquals(mealLog.getUserId(), savedMealLog.getUserId());
        Assertions.assertEquals(mealLog.getMealTime(), savedMealLog.getMealTime());
        Mockito.verify(kafkaProducerService, Mockito.times(1)).sendReportEvent(jsonMessage);
    }

    @Test
    void getMealLogById_Success() {
        Long mealLogId = 1L;
        Mockito.when(mealLogRepository.findById(mealLogId)).thenReturn(Optional.of(mealLog));

        MealLog result = mealLogService.getMealLogById(mealLogId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(mealLog.getUserId(), result.getUserId());
        Assertions.assertEquals(mealLog.getMealTime(), result.getMealTime());
    }

    @Test
    void updateMealLog_Success() throws JsonProcessingException {
        Long mealLogId = 1L;
        Long dishId = 1L;

        DishResponseDTO dishResponse = new DishResponseDTO();
        dishResponse.setCaloriesPerPortion(100.0);

        MealDish dish = new MealDish();
        dish.setDishId(dishId);
        dish.setQuantity(2);
        List<MealDish> mutableDishes = new ArrayList<>();
        mutableDishes.add(dish);
        mealLog.setDishes(mutableDishes);

        Mockito.when(dishServiceClient.getDishById(dishId)).thenReturn(dishResponse);
        Mockito.when(mealLogRepository.findById(mealLogId)).thenReturn(Optional.of(mealLog));
        Mockito.when(mealLogRepository.save(Mockito.any())).thenReturn(mealLog);

        ObjectMapper mapper = new JacksonConfig().objectMapper();
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", mealLog.getUserId());
        eventData.put("reportDate", mealLog.getMealTime());
        List<Map<String, Object>> dishesData = new ArrayList<>();
        for (MealDish dishFromMealLog : mealLog.getDishes()) {
            Map<String, Object> dishData = new HashMap<>();
            dishData.put("dishId", dishFromMealLog.getDishId());
            dishData.put("quantity", dishFromMealLog.getQuantity());
            dishData.put("calories", (int) (dishResponse.getCaloriesPerPortion() * dishFromMealLog.getQuantity()));
            dishesData.add(dishData);
        }
        eventData.put("dishes", dishesData);

        String expectedJson = mapper.writeValueAsString(eventData);

        MealLog updatedMealLog = mealLogService.updateMealLog(mealLogId, mealLog);

        Assertions.assertNotNull(updatedMealLog);
        Assertions.assertEquals(mealLog.getUserId(), updatedMealLog.getUserId());
        Assertions.assertEquals(mealLog.getMealTime(), updatedMealLog.getMealTime());

        Assertions.assertEquals(1, updatedMealLog.getDishes().size());
        Assertions.assertEquals(dishId, updatedMealLog.getDishes().get(0).getDishId());
        Assertions.assertEquals(2, updatedMealLog.getDishes().get(0).getQuantity());

        Mockito.verify(kafkaProducerService, Mockito.times(1)).sendReportEvent(expectedJson);
    }


    @Test
    void getMealLogById_NotFound() {
        Long mealLogId = 999L;
        Mockito.when(mealLogRepository.findById(mealLogId)).thenReturn(Optional.empty());

        MealLogNotFoundException exception = Assertions.assertThrows(
                MealLogNotFoundException.class,
                () -> mealLogService.getMealLogById(mealLogId)
        );

        Assertions.assertEquals("Meal log with ID 999 not found", exception.getMessage());
    }

    @Test
    void deleteMealLog_NotFound() {
        Long mealLogId = 999L;
        Mockito.when(mealLogRepository.existsById(mealLogId)).thenReturn(false);

        MealLogNotFoundException exception = Assertions.assertThrows(
                MealLogNotFoundException.class,
                () -> mealLogService.deleteMealLog(mealLogId)
        );

        Assertions.assertEquals("Meal log with ID 999 not found", exception.getMessage());
    }


}

