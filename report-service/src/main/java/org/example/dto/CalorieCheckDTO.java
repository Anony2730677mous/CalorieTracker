package org.example.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CalorieCheckDTO {
    private boolean isWithinGoal;
    private int dailyCalorieGoal;
    private int consumedCalories;

    public CalorieCheckDTO(boolean isWithinGoal, int dailyCalorieGoal, int consumedCalories) {
        this.isWithinGoal = isWithinGoal;
        this.dailyCalorieGoal = dailyCalorieGoal;
        this.consumedCalories = consumedCalories;
    }

    public boolean isWithinGoal() {
        return isWithinGoal;
    }

    public void setWithinGoal(boolean withinGoal) {
        isWithinGoal = withinGoal;
    }

    public int getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public void setDailyCalorieGoal(int dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
    }

    public int getConsumedCalories() {
        return consumedCalories;
    }

    public void setConsumedCalories(int consumedCalories) {
        this.consumedCalories = consumedCalories;
    }
}
