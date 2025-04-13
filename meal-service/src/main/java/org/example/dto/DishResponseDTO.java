package org.example.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DishResponseDTO {
    private Long id;
    private Double caloriesPerPortion;
    private String name;
    private Double proteinsPer100g; // Поле для протеинов
    private Double fatsPer100g;     // Поле для жиров
    private Double carbsPer100g;    // Поле для углеводов

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCaloriesPerPortion() {
        return caloriesPerPortion;
    }

    public void setCaloriesPerPortion(Double caloriesPerPortion) {
        this.caloriesPerPortion = caloriesPerPortion;
    }
}
