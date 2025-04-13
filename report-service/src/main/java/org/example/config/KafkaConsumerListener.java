package org.example.config;


import lombok.extern.slf4j.Slf4j;
import org.example.service.MealEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumerListener {
    private final Logger log = LoggerFactory.getLogger(KafkaConsumerListener.class);

    private final MealEventProcessor mealEventProcessor;

    public KafkaConsumerListener(MealEventProcessor mealEventProcessor) {
        this.mealEventProcessor = mealEventProcessor;
    }

    @KafkaListener(topics = "report-events", groupId = "report-service")
    public void consumeMealEvent(String message) {
        log.info("Received message from Kafka: {}", message);
        try {
            mealEventProcessor.processMealEvent(message);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
    }

}

