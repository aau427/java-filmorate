package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@EqualsAndHashCode(of = {"id"})
@Getter
public class User {
    @Setter
    private int id;
    @Email
    @NotBlank
    @Size(max = 50)
    private String email;
    @NotBlank
    @Size(max = 50)
    private String login;
    @Setter
    @Size(max = 50)
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

    public void deleteFriend(int friendId) {
        friendsList.remove(friendId);
    }
}
