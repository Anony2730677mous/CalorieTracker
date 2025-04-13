package org.example.exception;


public class MealLogNotFoundException extends RuntimeException {
    public MealLogNotFoundException(String message) {
        super(message);
    }
}
