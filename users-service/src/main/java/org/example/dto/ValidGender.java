package org.example.dto;

import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = GenderValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGender {

    String message() default "Invalid gender. Allowed values are MALE or FEMALE.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
