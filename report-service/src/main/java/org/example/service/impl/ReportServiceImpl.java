package org.example.service.impl;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CalorieCheckDTO;
import org.example.dto.DailyReportDTO;
import org.example.dto.UserDTO;
import org.example.entity.DailyReport;
import org.example.entity.MealReport;
import org.example.enums.Gender;
import org.example.exception.ReportNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.repository.ReportRepository;
import org.example.service.ReportService;
import org.example.service.UserServiceClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {
    private static final double BMR_MALE_CONSTANT = 88.362;
    private static final double BMR_FEMALE_CONSTANT = 447.593;
    private static final double WEIGHT_MULTIPLIER_MALE = 13.397;
    private static final double HEIGHT_MULTIPLIER_MALE = 4.799;
    private static final double AGE_MULTIPLIER_MALE = 5.677;
    private static final double WEIGHT_MULTIPLIER_FEMALE = 9.247;
    private static final double HEIGHT_MULTIPLIER_FEMALE = 3.098;
    private static final double AGE_MULTIPLIER_FEMALE = 4.330;

    private static final double ACTIVITY_LEVEL_WEIGHT_LOSS = 1.2;
    private static final double ACTIVITY_LEVEL_MAINTENANCE = 1.375;
    private static final double ACTIVITY_LEVEL_WEIGHT_GAIN = 1.725;

    private final ReportRepository reportRepository;
    private final Logger logger = Logger.getLogger("report");

    private final UserServiceClient userServiceClient;

    public ReportServiceImpl(ReportRepository reportRepository, UserServiceClient userServiceClient) {
        this.reportRepository = reportRepository;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public DailyReportDTO getDailyReport(Long userId, LocalDateTime reportDate) {
        checkUserExistence(userId);
        DailyReport report = reportRepository.findByUserIdAndReportDate(userId, reportDate)
                .orElseThrow(() -> new ReportNotFoundException("Report not found for user " + userId + " on date " + reportDate));
        return DailyReportDTO.fromEntity(report);
    }

    @Override
    public List<DailyReportDTO> getReportHistory(Long userId) {
        checkUserExistence(userId);
        List<DailyReport> reports = reportRepository.findAllByUserId(userId);
        return reports.stream()
                .map(DailyReportDTO::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    public CalorieCheckDTO checkCalorieGoal(Long userId, LocalDateTime reportDate) {
        checkUserExistence(userId);
        DailyReport report = reportRepository.findByUserIdAndReportDate(userId, reportDate)
                .orElseThrow(() -> new ReportNotFoundException("Report not found for user " + userId + " on date " + reportDate));

        UserDTO user = userServiceClient.getUserById(userId);
        logger.info("Get user with id: " + user.getId());

        int dailyCalorieGoal = calculateDailyCalorieGoal(user);

        boolean isWithinGoal = report.getTotalCalories() <= dailyCalorieGoal;

        return new CalorieCheckDTO(isWithinGoal, dailyCalorieGoal, report.getTotalCalories());
    }


    @Override
    @Transactional
    public void updateReport(Long userId, List<Map<String, Object>> dishes, LocalDateTime reportDate) {
        checkUserExistence(userId);
        DailyReport dailyReport = reportRepository.findByUserIdAndReportDate(userId, reportDate)
                .orElseGet(() -> {
                    DailyReport newReport = new DailyReport();
                    newReport.setUserId(userId);
                    newReport.setReportDate(reportDate);
                    newReport.setTotalCalories(0);
                    newReport.setMealReports(new ArrayList<>());
                    return reportRepository.save(newReport);
                });

        int totalCalories = dailyReport.getTotalCalories();

        for (Map<String, Object> dishData : dishes) {
            Long dishId = ((Number) dishData.get("dishId")).longValue();
            int quantity = ((Number) dishData.get("quantity")).intValue();
            int calories = ((Number) dishData.get("calories")).intValue();

            totalCalories += calories;

            MealReport mealReport = new MealReport();
            mealReport.setDailyReport(dailyReport);
            mealReport.setDishId(dishId);
            mealReport.setQuantity(quantity);
            mealReport.setCalories(calories);

            dailyReport.getMealReports().add(mealReport);
        }

        dailyReport.setTotalCalories(totalCalories);
        reportRepository.save(dailyReport);
    }

    @Override
    public CalorieCheckDTO checkDailyCalorieGoal(Long userId, LocalDateTime reportDate) {
        checkUserExistence(userId);

        LocalDateTime startOfDay = reportDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = reportDate.toLocalDate().atTime(23, 59, 59);

        List<DailyReport> dailyReports = reportRepository.findAllByUserId(userId).stream()
                .filter(report -> !report.getReportDate().isBefore(startOfDay) && !report.getReportDate().isAfter(endOfDay))
                .collect(Collectors.toList());

        if (dailyReports.isEmpty()) {
            throw new ReportNotFoundException("No reports found for user " + userId + " on date " + reportDate.toLocalDate());
        }

        int totalCalories = dailyReports.stream()
                .mapToInt(DailyReport::getTotalCalories)
                .sum();

        UserDTO user = userServiceClient.getUserById(userId);

        int dailyCalorieGoal = calculateDailyCalorieGoal(user);

        boolean isWithinGoal = totalCalories <= dailyCalorieGoal;

        return new CalorieCheckDTO(isWithinGoal, dailyCalorieGoal, totalCalories);
    }


    private int calculateDailyCalorieGoal(UserDTO user) {
        double bmr = user.getGender() == Gender.MALE
                ? BMR_MALE_CONSTANT + (WEIGHT_MULTIPLIER_MALE * user.getWeight()) + (HEIGHT_MULTIPLIER_MALE * user.getHeight()) - (AGE_MULTIPLIER_MALE * user.getAge())
                : BMR_FEMALE_CONSTANT + (WEIGHT_MULTIPLIER_FEMALE * user.getWeight()) + (HEIGHT_MULTIPLIER_FEMALE * user.getHeight()) - (AGE_MULTIPLIER_FEMALE * user.getAge());

        double activityLevel = switch (user.getGoal()) {
            case WEIGHT_LOSS -> ACTIVITY_LEVEL_WEIGHT_LOSS;
            case MAINTENANCE -> ACTIVITY_LEVEL_MAINTENANCE;
            case WEIGHT_GAIN -> ACTIVITY_LEVEL_WEIGHT_GAIN;
            default -> ACTIVITY_LEVEL_WEIGHT_LOSS;
        };

        return (int) (bmr * activityLevel);
    }

    private void checkUserExistence(Long userId) {
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException.NotFound ex) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
    }

}

