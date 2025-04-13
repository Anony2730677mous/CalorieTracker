package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.entity.MealLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class MealLogDTO {

    private Long mealLogId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Dishes cannot be null")
    private List<MealDishDTO> dishes;

    @NotNull(message = "Meal time cannot be null")
    private LocalDateTime mealTime;

    public MealLog toEntity() {
        MealLog mealLog = new MealLog();
        mealLog.setUserId(this.userId);
        mealLog.setMealTime(this.mealTime);
        mealLog.setDishes(this.dishes.stream()
                .map(MealDishDTO::toEntity)
                .peek(dish -> dish.setMealLog(mealLog)) // Устанавливаем связь
                .collect(Collectors.toList()));
        return mealLog;
    }

    public static MealLogDTO fromEntity(MealLog mealLog) {
        MealLogDTO dto = new MealLogDTO();
        dto.setMealLogId(mealLog.getId());
        dto.setUserId(mealLog.getUserId());
        dto.setMealTime(mealLog.getMealTime());
        dto.setDishes(mealLog.getDishes().stream()
                .map(MealDishDTO::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }

    private void setMealLogId(Long mealLogId) {
        this.mealLogId = mealLogId;
    }

    public Long getMealLogId() {
        return mealLogId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<MealDishDTO> getDishes() {
        return dishes;
    }

    public void setDishes(List<MealDishDTO> dishes) {
        this.dishes = dishes;
    }

    public LocalDateTime getMealTime() {
        return mealTime;
    }

    public void setMealTime(LocalDateTime mealTime) {
        this.mealTime = mealTime;
    }

}
