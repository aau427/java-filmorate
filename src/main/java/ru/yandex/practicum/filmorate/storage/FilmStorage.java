package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(final Film film);

    Film updateFilm(final Film film);

    List<Film> getFilmsList();

    Optional<Film> getFilmById(int id);

    void deleteAllFilms();

    void setUsersLike(Film film, int userId);

    void deleteUsersLike(Film film, int userId);

    List<Film> getTopFilmsByLikes(int countFilms);
}
