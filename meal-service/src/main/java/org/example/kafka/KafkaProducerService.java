package org.example.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEvent(String message) {
        kafkaTemplate.send("user-events", message);
    }

    public void sendDishEvent(String message) {
        kafkaTemplate.send("dish-events", message);
    }

    public void sendReportEvent(String message) {
        kafkaTemplate.send("report-events", message);
    }
}
