package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.entity.MealDish;

@Getter
@Setter
@RequiredArgsConstructor
public class MealDishDTO {

    @NotNull(message = "Dish ID cannot be null")
    private Long dishId;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;

    public MealDish toEntity() {
        MealDish mealDish = new MealDish();
        mealDish.setDishId(this.dishId);
        mealDish.setQuantity(this.quantity);
        return mealDish;
    }

    public static MealDishDTO fromEntity(MealDish mealDish) {
        MealDishDTO dto = new MealDishDTO();
        dto.setDishId(mealDish.getDishId());
        dto.setQuantity(mealDish.getQuantity());
        return dto;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}