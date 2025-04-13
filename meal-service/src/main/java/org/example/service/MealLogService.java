package org.example.service;

import org.example.entity.MealLog;

import java.util.List;

public interface MealLogService {
    List<MealLog> getAllMealLogs();
    MealLog getMealLogById(Long id);
    MealLog createMealLog(MealLog mealLog);
    MealLog updateMealLog(Long id, MealLog mealLog);
    void deleteMealLog(Long id);
    List<MealLog> getMealLogsByUserId(Long userId);

}