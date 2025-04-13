package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.entity.Dish;

@Getter
@Setter
@RequiredArgsConstructor
public class DishDTO {
    private Long id;

    @NotNull(message = "Dish name cannot be null")
    @NotBlank(message = "Dish name cannot be blank")
    private String name;

    @NotNull(message = "Calories per portion cannot be null")
    @Positive(message = "Calories per portion must be greater than 0")
    private Double caloriesPerPortion;

    @NotNull(message = "Proteins per 100 grams cannot be null")
    @Positive(message = "Proteins per 100 grams must be greater than 0")
    private Double proteinsPer100g;

    @NotNull(message = "Fats per 100 grams cannot be null")
    @Positive(message = "Fats per 100 grams must be greater than 0")
    private Double fatsPer100g;

    @NotNull(message = "Carbs per 100 grams cannot be null")
    @Positive(message = "Carbs per 100 grams must be greater than 0")
    private Double carbsPer100g;

    public Dish toEntity() {
        Dish dish = new Dish();
        dish.setName(this.name);
        dish.setCaloriesPerPortion(this.caloriesPerPortion);
        dish.setProteinsPer100g(this.proteinsPer100g);
        dish.setFatsPer100g(this.fatsPer100g);
        dish.setCarbsPer100g(this.carbsPer100g);
        return dish;
    }

    public static DishDTO fromEntity(Dish dish) {
        DishDTO dto = new DishDTO();
        dto.setId(dish.getId());
        dto.setName(dish.getName());
        dto.setCaloriesPerPortion(dish.getCaloriesPerPortion());
        dto.setProteinsPer100g(dish.getProteinsPer100g());
        dto.setFatsPer100g(dish.getFatsPer100g());
        dto.setCarbsPer100g(dish.getCarbsPer100g());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCaloriesPerPortion() {
        return caloriesPerPortion;
    }

    public void setCaloriesPerPortion(Double caloriesPerPortion) {
        this.caloriesPerPortion = caloriesPerPortion;
    }

    public Double getProteinsPer100g() {
        return proteinsPer100g;
    }

    public void setProteinsPer100g(Double proteinsPer100g) {
        this.proteinsPer100g = proteinsPer100g;
    }

    public Double getFatsPer100g() {
        return fatsPer100g;
    }

    public void setFatsPer100g(Double fatsPer100g) {
        this.fatsPer100g = fatsPer100g;
    }

    public Double getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(Double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }
}
