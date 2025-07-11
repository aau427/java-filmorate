package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.exception.CloneException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@EqualsAndHashCode(of = {"id"})
@Getter
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


    private final Set<Integer> friendsList = new HashSet<>();

    @JsonCreator
    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        if (name == null) {
            this.name = login;
        } else {
            this.name = name;
        }
    }

    public void addFriend(int friendId) {
        friendsList.add(friendId);
    }

    @Override
    public User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneException("Ошибка при копировании пользователя!");
        }
    }

    public void deleteFriend(int friendId) {
        friendsList.remove(friendId);
    }
}
