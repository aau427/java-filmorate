package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.CloneException;

import java.time.LocalDate;

@Data
public class User implements Cloneable {
    /*
    электронная почта не может быть пустой и должна содержать символ @;
    логин не может быть пустым и содержать пробелы;
    имя для отображения может быть пустым — в таком случае будет использован логин;
    дата рождения не может быть в будущем.
     */
    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
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
