package org.example.repository;

import org.example.entity.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    List<MealLog> findByUserId(Long userId);
}
