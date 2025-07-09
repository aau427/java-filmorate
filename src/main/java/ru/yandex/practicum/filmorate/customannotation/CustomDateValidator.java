package ru.yandex.practicum.filmorate.customannotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class CustomDateValidator implements ConstraintValidator<CustomValidDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate iDate, ConstraintValidatorContext context) {
        log.info("Сработал кастомный валидатор даты!");
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 27);
        return minReleaseDate.isBefore(iDate);
    }
}
