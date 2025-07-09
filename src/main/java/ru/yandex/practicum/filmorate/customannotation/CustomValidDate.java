package ru.yandex.practicum.filmorate.customannotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomDateValidator.class)
public @interface CustomValidDate {
    String message() default "Ошибка! Дата релиза не может быть ранее дня рождения кино";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
