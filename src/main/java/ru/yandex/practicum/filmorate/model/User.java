package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;
import ru.yandex.practicum.filmorate.exception.CloneException;

import java.time.LocalDate;

@EqualsAndHashCode(of = {"id"})
@Getter
@Builder
public class User implements Cloneable {
    @Setter
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    @Setter
    private String name;
    @Past
    private LocalDate birthday;

    @Override
    public User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneException("Ошибка при копировании пользователя!");
        }
    }
}
