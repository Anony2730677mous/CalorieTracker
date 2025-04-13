package org.example.controller;


import org.example.dto.CalorieCheckDTO;
import org.example.dto.DailyReportDTO;
import org.example.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily/{userId}")
    public ResponseEntity<DailyReportDTO> getDailyReport(@PathVariable Long userId, @RequestParam LocalDateTime reportDate) {
        DailyReportDTO report = reportService.getDailyReport(userId, reportDate);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<DailyReportDTO>> getReportHistory(@PathVariable Long userId) {
        List<DailyReportDTO> reports = reportService.getReportHistory(userId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/calorie-check/{userId}")
    public ResponseEntity<CalorieCheckDTO> checkCalorieGoal(@PathVariable Long userId, @RequestParam LocalDateTime reportDate) {
        CalorieCheckDTO check = reportService.checkCalorieGoal(userId, reportDate);
        return ResponseEntity.ok(check);
    }

    @GetMapping("/calorie-check/daily/{userId}")
    public ResponseEntity<CalorieCheckDTO> checkDailyCalorieGoal(
            @PathVariable Long userId,
            @RequestParam String reportDate) {
        LocalDateTime parsedDate;

        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            parsedDate = LocalDateTime.parse(reportDate, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            parsedDate = LocalDate.parse(reportDate, dateFormatter).atStartOfDay();
        }
        CalorieCheckDTO check = reportService.checkDailyCalorieGoal(userId, parsedDate);
        return ResponseEntity.ok(check);
    }

}

