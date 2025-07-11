package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.customannotation.CustomValidDate;
import ru.yandex.practicum.filmorate.exception.CloneException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = {"id"})
@Getter
@Builder
public class Film implements Cloneable {
    @Setter
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    @CustomValidDate
    private LocalDate releaseDate;
    @Positive
    private int duration;
    //лайки пользователей (юзеров)
    private final Set<Integer> userLikes = new HashSet<>();

    public void setLike(int userId) {
        userLikes.add(userId);
    }

    public void deleteLike(int userId) {
        userLikes.remove(userId);
    }

    public int getCountLikes() {
        return userLikes.size();
    }

    @Override
    public Film clone() {
        try {
            return (Film) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneException("Ошибка при копировании фильма!");
        }
    }
}
