package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.MealLogDTO;
import org.example.entity.MealLog;
import org.example.service.MealLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meals")
public class MealLogController {

    private final MealLogService mealLogService;

    public MealLogController(MealLogService mealLogService) {
        this.mealLogService = mealLogService;
    }

    @GetMapping
    public List<MealLogDTO> getAllMealLogs() {
        return mealLogService.getAllMealLogs().stream()
                .map(MealLogDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealLogDTO> getMealLogById(@PathVariable Long id) {
        MealLog mealLog = mealLogService.getMealLogById(id);
        return ResponseEntity.ok(MealLogDTO.fromEntity(mealLog));
    }

    @PostMapping
    public ResponseEntity<MealLogDTO> createMealLog(@Valid @RequestBody MealLogDTO mealLogDTO) {
        MealLog mealLog = mealLogDTO.toEntity();
        MealLog createdMealLog = mealLogService.createMealLog(mealLog);
        return ResponseEntity.ok(MealLogDTO.fromEntity(createdMealLog));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealLogDTO> updateMealLog(@PathVariable Long id, @Valid @RequestBody MealLogDTO mealLogDTO) {
        MealLog mealLog = mealLogDTO.toEntity();
        MealLog updatedMealLog = mealLogService.updateMealLog(id, mealLog);
        return ResponseEntity.ok(MealLogDTO.fromEntity(updatedMealLog));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealLog(@PathVariable Long id) {
        mealLogService.deleteMealLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public List<MealLogDTO> getMealLogsByUserId(@PathVariable("id") Long userId) {
        return mealLogService.getMealLogsByUserId(userId).stream()
                .map(MealLogDTO::fromEntity)
                .collect(Collectors.toList());
    }


}
