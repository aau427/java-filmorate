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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@EqualsAndHashCode(of = {"id"})
@Getter
public class Film {
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
    private Integer duration;
    private final Set<Integer> userLikes = new HashSet<>();
    private Rating mpa;

    private final LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    public void setLike(int userId) {
        userLikes.add(userId);
    }

    public void setGenre(Genre genre) {
        genres.add(genre);
    }

    public void deleteLike(int userId) {
        userLikes.remove(userId);
    }

    public int getCountLikes() {
        return userLikes.size();
    }
}
