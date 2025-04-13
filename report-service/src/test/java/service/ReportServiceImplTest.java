package service;

import feign.FeignException;
import feign.Request;
import org.example.dto.CalorieCheckDTO;
import org.example.dto.DailyReportDTO;
import org.example.dto.UserDTO;
import org.example.entity.DailyReport;
import org.example.enums.Gender;
import org.example.enums.Goal;
import org.example.exception.ReportNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.repository.ReportRepository;
import org.example.service.UserServiceClient;
import org.example.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ReportServiceImpl reportService;

    private UserDTO user;

    @BeforeEach
    void setUp() {
        user = new UserDTO();
        user.setId(7L);
        user.setGender(Gender.FEMALE);
        user.setWeight(50.0);
        user.setHeight(170.0);
        user.setAge(20);
        user.setGoal(Goal.WEIGHT_GAIN);
    }

    @Test
    void getDailyReport_Success() {
        Long userId = 7L;
        LocalDateTime reportDate = LocalDateTime.of(2025, 4, 6, 12, 0, 0);
        DailyReport dailyReport = new DailyReport();
        dailyReport.setUserId(userId);
        dailyReport.setReportDate(reportDate);
        dailyReport.setTotalCalories(1000);
        dailyReport.setMealReports(new ArrayList<>());

        Mockito.when(userServiceClient.getUserById(userId)).thenReturn(user);
        Mockito.when(reportRepository.findByUserIdAndReportDate(userId, reportDate))
                .thenReturn(Optional.of(dailyReport));

        DailyReportDTO result = reportService.getDailyReport(userId, reportDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId, result.getUserId());
        Assertions.assertEquals(reportDate, result.getReportDate());
        Assertions.assertEquals(1000, result.getTotalCalories());
    }

    @Test
    void getDailyReport_ReportNotFound() {
        Long userId = 7L;
        LocalDateTime reportDate = LocalDateTime.of(2025, 4, 6, 12, 0, 0);

        Mockito.when(userServiceClient.getUserById(userId)).thenReturn(user);
        Mockito.when(reportRepository.findByUserIdAndReportDate(userId, reportDate))
                .thenReturn(Optional.empty());

        ReportNotFoundException exception = Assertions.assertThrows(
                ReportNotFoundException.class,
                () -> reportService.getDailyReport(userId, reportDate)
        );

        Assertions.assertEquals("Report not found for user 7 on date 2025-04-06T12:00", exception.getMessage());
    }

    @Test
    void checkDailyCalorieGoal_Success() {
        Long userId = 7L;
        LocalDateTime reportDate = LocalDateTime.of(2025, 4, 6, 12, 0, 0);
        DailyReport dailyReport1 = new DailyReport();
        dailyReport1.setUserId(userId);
        dailyReport1.setReportDate(reportDate.toLocalDate().atStartOfDay());
        dailyReport1.setTotalCalories(2000);

        DailyReport dailyReport2 = new DailyReport();
        dailyReport2.setUserId(userId);
        dailyReport2.setReportDate(reportDate.toLocalDate().atTime(23, 59, 59));
        dailyReport2.setTotalCalories(2500);

        Mockito.when(userServiceClient.getUserById(userId)).thenReturn(user);
        Mockito.when(reportRepository.findAllByUserId(userId)).thenReturn(List.of(dailyReport1, dailyReport2));

        CalorieCheckDTO result = reportService.checkDailyCalorieGoal(userId, reportDate);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2328, result.getDailyCalorieGoal());
        Assertions.assertEquals(4500, result.getConsumedCalories());
        Assertions.assertFalse(result.isWithinGoal());
    }

    @Test
    void updateReport_Success() {
        Long userId = 7L;
        LocalDateTime reportDate = LocalDateTime.of(2025, 4, 6, 12, 0, 0);
        List<Map<String, Object>> dishes = List.of(
                Map.of("dishId", 1L, "quantity", 3, "calories", 300),
                Map.of("dishId", 2L, "quantity", 2, "calories", 200)
        );

        DailyReport existingReport = new DailyReport();
        existingReport.setUserId(userId);
        existingReport.setReportDate(reportDate);
        existingReport.setTotalCalories(1000);
        existingReport.setMealReports(new ArrayList<>()); // Обязательно инициализируем!


        Mockito.when(userServiceClient.getUserById(userId)).thenReturn(user);
        Mockito.when(reportRepository.findByUserIdAndReportDate(userId, reportDate))
                .thenReturn(Optional.of(existingReport));

        reportService.updateReport(userId, dishes, reportDate);

        Mockito.verify(reportRepository, Mockito.times(1)).save(existingReport);
        Assertions.assertEquals(1500, existingReport.getTotalCalories());
        Assertions.assertEquals(2, existingReport.getMealReports().size());
    }

    @Test
    void checkUserExistence_UserNotFound() {
        Long userId = 999L;

        Mockito.when(userServiceClient.getUserById(userId)).thenThrow(
                new FeignException.NotFound(
                        "User not found",
                        Mockito.mock(Request.class),
                        null,
                        null
                )
        );
        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> reportService.checkDailyCalorieGoal(userId, LocalDateTime.now())
        );

        Assertions.assertEquals("User with ID 999 not found", exception.getMessage());
    }

    @Test
    void checkDailyCalorieGoal_UserNotFound() {
        Long userId = 999L;
        LocalDateTime reportDate = LocalDateTime.of(2025, 4, 6, 12, 0, 0);

        Mockito.when(userServiceClient.getUserById(userId)).thenThrow(
                new FeignException.NotFound(
                        "User not found",
                        Mockito.mock(Request.class),
                        null,
                        null
                )
        );

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> reportService.checkDailyCalorieGoal(userId, reportDate)
        );

        Assertions.assertEquals("User with ID 999 not found", exception.getMessage());
    }

    @Test
    void updateReport_UserNotFound() {
        Long userId = 999L;
        LocalDateTime reportDate = LocalDateTime.of(2025, 4, 6, 12, 0, 0);
        List<Map<String, Object>> dishes = List.of(
                Map.of("dishId", 1L, "quantity", 3, "calories", 300),
                Map.of("dishId", 2L, "quantity", 2, "calories", 200)
        );

        Mockito.when(userServiceClient.getUserById(userId)).thenThrow(
                new FeignException.NotFound(
                        "User not found",
                        Mockito.mock(Request.class),
                        null,
                        null
                )
        );

        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> reportService.updateReport(userId, dishes, reportDate)
        );

        Assertions.assertEquals("User with ID 999 not found", exception.getMessage());
    }

    @Test
    void checkCalorieGoal_ReportNotFound() {
        Long userId = 7L;
        LocalDateTime reportDate = LocalDateTime.of(2025, 4, 6, 12, 0, 0);

        Mockito.when(userServiceClient.getUserById(userId)).thenReturn(user);
        Mockito.when(reportRepository.findByUserIdAndReportDate(userId, reportDate))
                .thenReturn(Optional.empty());

        ReportNotFoundException exception = Assertions.assertThrows(
                ReportNotFoundException.class,
                () -> reportService.checkCalorieGoal(userId, reportDate)
        );

        Assertions.assertEquals("Report not found for user 7 on date 2025-04-06T12:00", exception.getMessage());
    }


}

