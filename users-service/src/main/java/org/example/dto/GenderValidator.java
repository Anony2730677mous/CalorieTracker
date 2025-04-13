package org.example.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.entity.enums.Gender;

public class GenderValidator implements ConstraintValidator<ValidGender, Gender> {

    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value == Gender.MALE || value == Gender.FEMALE;
    }
}
