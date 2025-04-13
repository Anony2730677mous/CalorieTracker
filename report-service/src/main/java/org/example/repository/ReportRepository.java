package org.example.repository;


import org.example.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<DailyReport, Long> {
    Optional<DailyReport> findByUserIdAndReportDate(Long userId, LocalDateTime reportDate);

    List<DailyReport> findAllByUserId(Long userId);
}
