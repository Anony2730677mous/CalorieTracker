package org.example.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.entity.DailyReport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class DailyReportDTO {
    private Long id;
    private Long userId;
    private LocalDateTime reportDate;
    private int totalCalories;
    private List<MealReportDTO> mealReports;


    public static DailyReportDTO fromEntity(DailyReport dailyReport) {
        DailyReportDTO dto = new DailyReportDTO();
        dto.setId(dailyReport.getId());
        dto.setUserId(dailyReport.getUserId());
        dto.setReportDate(dailyReport.getReportDate());
        dto.setTotalCalories(dailyReport.getTotalCalories());
        dto.setMealReports(
                dailyReport.getMealReports() != null
                        ? dailyReport.getMealReports().stream()
                        .map(MealReportDTO::fromEntity)
                        .collect(Collectors.toList())
                        : new ArrayList<>()
        );
        return dto;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public List<MealReportDTO> getMealReports() {
        return mealReports;
    }

    public void setMealReports(List<MealReportDTO> mealReports) {
        this.mealReports = mealReports;
    }
}

