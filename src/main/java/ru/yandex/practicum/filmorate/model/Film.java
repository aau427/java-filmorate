package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.CloneException;

import java.time.LocalDate;

@Data
public class Film implements Cloneable{
    /*
    название не может быть пустым;
    максимальная длина описания — 200 символов;
    дата релиза — не раньше 28 декабря 1895 года;
    продолжительность фильма должна быть положительным числом.
     */
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;

    @Override
    public Film clone() {
        try {
            return (Film) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneException("Ошибка при копировании фильма!");
        }
    }
}
