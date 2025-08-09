package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingStorage {
    Optional<Rating> getRatingById(int idRating);

    List<Rating> getAllRatings();
}
