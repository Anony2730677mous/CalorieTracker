package org.example.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
/*
Методы класса не участвуют в обмене сообщениями, существуют для возможного расширения функционала сервиса
 */
@Component
public class KafkaConsumerListener {

    @KafkaListener(topics = "user-events", groupId = "meal-log-service")
    public void consumeUserEvent(String message) {
        System.out.println("Received user event: " + message);
    }

    @KafkaListener(topics = "dish-events", groupId = "meal-log-service")
    public void consumeDishEvent(String message) {
        System.out.println("Received dish event: " + message);
    }
}