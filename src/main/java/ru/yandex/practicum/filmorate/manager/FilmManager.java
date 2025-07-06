package ru.yandex.practicum.filmorate.manager;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmManager {
    Film createFilm(final Film film);

    Film updateFilm(final Film film);

    List<Film> getFilmsList();
}
