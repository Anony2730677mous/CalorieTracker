package org.example.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.entity.MealReport;

@RequiredArgsConstructor
@Getter
@Setter
public class MealReportDTO {
    private Long id;
    private Long dishId;
    private int quantity;
    private int calories;


    public static MealReportDTO fromEntity(MealReport mealReport) {
        MealReportDTO dto = new MealReportDTO();
        dto.setId(mealReport.getId());
        dto.setDishId(mealReport.getDishId());
        dto.setQuantity(mealReport.getQuantity());
        dto.setCalories(mealReport.getCalories());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}

