package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode(of = "id")
@RequiredArgsConstructor
public class Rating {
    private final int id;
    @NotBlank
    @Size(max = 20)
    private final String name;
}
