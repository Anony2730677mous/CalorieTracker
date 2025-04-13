package org.example.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DishResponseDTO;
import org.example.entity.MealDish;
import org.example.entity.MealLog;
import org.example.exception.MealLogNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.kafka.KafkaProducerService;
import org.example.repository.MealLogRepository;
import org.example.service.DishServiceClient;
import org.example.service.MealLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MealLogServiceImpl implements MealLogService {
    private final Logger log = LoggerFactory.getLogger(MealLogServiceImpl.class);


    private final MealLogRepository mealLogRepository;
    private final KafkaProducerService kafkaProducerService;
    private final DishServiceClient dishServiceClient;
    private final ObjectMapper objectMapper;

    public MealLogServiceImpl(MealLogRepository mealLogRepository,
                              KafkaProducerService kafkaProducerService,
                              DishServiceClient dishServiceClient,
                              ObjectMapper objectMapper) {
        this.mealLogRepository = mealLogRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.dishServiceClient = dishServiceClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<MealLog> getAllMealLogs() {
        return mealLogRepository.findAll();
    }

    @Override
    public MealLog getMealLogById(Long id) {
        return mealLogRepository.findById(id)
                .orElseThrow(() -> new MealLogNotFoundException("Meal log with ID " + id + " not found"));
    }

    @Override
    public List<MealLog> getMealLogsByUserId(Long userId) {
        List<MealLog> mealLogs = mealLogRepository.findByUserId(userId);

        if (mealLogs.isEmpty()) {
            throw new UserNotFoundException("User with ID " + userId + " not found in meal logs.");
        }

        List<MealLog> validMealLogs = mealLogs.stream()
                .filter(mealLog -> mealLog.getMealTime() != null && mealLog.getDishes() != null)
                .collect(Collectors.toList());

        if (validMealLogs.isEmpty()) {
            return Collections.emptyList();
        }

        return validMealLogs;
    }


    @Override
    @Transactional
    public MealLog createMealLog(MealLog mealLog) {
        log.info("Creating meal log for user ID: {}, meal time: {}", mealLog.getUserId(), mealLog.getMealTime());

        MealLog savedMealLog = mealLogRepository.save(mealLog);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", savedMealLog.getUserId());
        eventData.put("reportDate", savedMealLog.getMealTime());
        List<Map<String, Object>> dishesData = new ArrayList<>();

        for (MealDish dish : savedMealLog.getDishes()) {
            Map<String, Object> dishData = new HashMap<>();
            dishData.put("dishId", dish.getDishId());
            dishData.put("quantity", dish.getQuantity());
            dishData.put("calories", calculateCalories(dish.getDishId(), dish.getQuantity()));
            dishesData.add(dishData);
        }
        eventData.put("dishes", dishesData);

        try {
            String eventMessage = objectMapper.writeValueAsString(eventData);
            log.info("Sending meal log event to Kafka topic 'meal-events': {}", eventMessage);
            kafkaProducerService.sendReportEvent(eventMessage);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize meal log event data: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process request", e);
        }

        return savedMealLog;
    }


    private int calculateCalories(Long dishId, int quantity) {
        DishResponseDTO dish = dishServiceClient.getDishById(dishId);
        return (int) (dish.getCaloriesPerPortion() * quantity);
    }

    @Override
    @Transactional
    public MealLog updateMealLog(Long id, MealLog mealLog) {
        MealLog existingMealLog = getMealLogById(id);

        existingMealLog.setUserId(mealLog.getUserId());
        existingMealLog.setMealTime(mealLog.getMealTime());
        List<MealDish> dishesToUpdate = new ArrayList<>(mealLog.getDishes());
        existingMealLog.getDishes().clear();

        for (MealDish dish : dishesToUpdate) {
            dish.setMealLog(existingMealLog);
            existingMealLog.getDishes().add(dish);
        }

        log.info("Updated ExistingMealLog dishes: {}", existingMealLog);

        MealLog updatedMealLog = mealLogRepository.save(existingMealLog);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", updatedMealLog.getUserId());
        eventData.put("reportDate", updatedMealLog.getMealTime());
        List<Map<String, Object>> dishesData = new ArrayList<>();
        for (MealDish dish : updatedMealLog.getDishes()) {
            Map<String, Object> dishData = new HashMap<>();
            dishData.put("dishId", dish.getDishId());
            dishData.put("quantity", dish.getQuantity());
            dishData.put("calories", calculateCalories(dish.getDishId(), dish.getQuantity()));
            dishesData.add(dishData);
        }
        eventData.put("dishes", dishesData);

        try {
            String eventMessage = objectMapper.writeValueAsString(eventData);
            kafkaProducerService.sendReportEvent(eventMessage);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize meal log data", e);
            throw new RuntimeException(e);
        }

        return updatedMealLog;
    }


    @Override
    @Transactional
    public void deleteMealLog(Long id) {
        if (!mealLogRepository.existsById(id)) {
            throw new MealLogNotFoundException("Meal log with ID " + id + " not found");
        }
        MealLog mealLog = getMealLogById(id);
        mealLogRepository.deleteById(id);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", mealLog.getUserId());
        eventData.put("reportDate", mealLog.getMealTime());

        List<Map<String, Object>> dishesData = new ArrayList<>();
        for (MealDish dish : mealLog.getDishes()) {
            Map<String, Object> dishData = new HashMap<>();
            dishData.put("dishId", dish.getDishId());
            dishData.put("quantity", dish.getQuantity());
            dishData.put("calories", calculateCalories(dish.getDishId(), dish.getQuantity()));
            dishesData.add(dishData);
        }
        eventData.put("dishes", dishesData);

        try {
            String eventMessage = objectMapper.writeValueAsString(eventData); // Используем инжектированный ObjectMapper
            kafkaProducerService.sendReportEvent(eventMessage);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize meal log data", e);
            throw new RuntimeException(e);
        }
    }

}
