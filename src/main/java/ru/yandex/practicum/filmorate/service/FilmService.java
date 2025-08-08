package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;

    private FilmService(@Qualifier("FILMDBSSTORAGE") FilmStorage storage,
                        UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public Film createFilm(Film film) {
        film.setId(getNextId());
        Film newFilm = storage.createFilm(film);
        log.info("Создан новый фильм {}", newFilm.getId());
        return newFilm.clone();
    }

    public Film updateFilm(Film film) {
        Film tmpFilm = getFilmById(film.getId());
        tmpFilm = storage.updateFilm(film);
        log.info("Изменил фильм: {}", tmpFilm.getId());
        return tmpFilm.clone();
    }

    public List<Film> getFilmsList() {
        List<Film> filmList = storage.getFilmsList();
        log.info("Отгрузил фильмы в количестве {}", filmList.size());
        return filmList;
    }


    public void deleteAllFilms() {
        storage.deleteAllFilms();
        log.info("Удаляю все фильмы");
    }

    public Film getFilmById(int filmId) {
        return storage.getFilmById(filmId)
                .orElseThrow(() -> {
                    String msg = "Не могу найти фильм с Id =" + filmId;
                    log.error(msg);
                    throw new ResourceNotFoundException(msg);
                });
    }

    public void setUsersLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);
        storage.setUsersLike(film, userId);
        String message = "Поставил лайк фильму " + filmId + " от пользователя " + userId;
        log.info(message);
    }

    public void deleteUsersLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);
        storage.deleteUsersLike(film, userId);
        String message = "Удалил лайк у фильма " + filmId + " от пользователя " + userId;
        log.info(message);
    }

    public List<Film> getTopFilmsByLikes(int countFilms) {
        if (countFilms == 0) {
            throw new ValidationException("Количество фильмов не может быть отрицательным!");
        }
        return storage.getTopFilmsByLikes(countFilms);
    }

    private int getNextId() {
        return getFilmsList().size() + 1;
    }
}