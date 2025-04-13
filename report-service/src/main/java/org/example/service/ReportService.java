package org.example.service;

import org.example.dto.CalorieCheckDTO;
import org.example.dto.DailyReportDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {
    DailyReportDTO getDailyReport(Long userId, LocalDateTime reportDate);

    List<DailyReportDTO> getReportHistory(Long userId);

    CalorieCheckDTO checkCalorieGoal(Long userId, LocalDateTime reportDate);

    void updateReport(Long userId, List<Map<String, Object>> dishes, LocalDateTime reportDate);

    CalorieCheckDTO checkDailyCalorieGoal(Long userId, LocalDateTime reportDate);
}
