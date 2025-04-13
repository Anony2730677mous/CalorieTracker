package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MealEventProcessor {
    private final Logger log = LoggerFactory.getLogger(MealEventProcessor.class);
    private final ReportService reportService;

    public MealEventProcessor(ReportService reportService) {
        this.reportService = reportService;
    }

    public void processMealEvent(String message) {
        log.info("Received message from Kafka: {}", message);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(message);
            Long userId = root.path("userId").asLong();
            LocalDateTime reportDate = LocalDateTime.parse(root.path("reportDate").asText());
            List<Map<String, Object>> dishes = new ArrayList<>();

            for (JsonNode dishNode : root.path("dishes")) {
                Map<String, Object> dishData = mapper.convertValue(dishNode, Map.class);
                dishes.add(dishData);
            }
            reportService.updateReport(userId, dishes, reportDate);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Kafka message: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during processing: {}", e.getMessage(), e);
        }
    }
}


