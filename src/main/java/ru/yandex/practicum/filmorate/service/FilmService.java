package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;

    public Film createFilm(final Film film) {
        film.setId(getNextId());
        Film newFilm = storage.createFilm(film);
        log.info("Создан новый фильм {}", newFilm.getId());
        return newFilm.clone();
    }

    public Film updateFilm(final Film film) {
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
        Optional<Film> filmOptional = storage.getFilmById(filmId);
        if (!filmOptional.isPresent()) {
            String msg = "Не нашел фильм с Id = " + filmId;
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return filmOptional.get();
    }

    public void setUsersLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);
        String message = "Поставил лайк фильму " + filmId + " от пользователя " + userId;
        log.info(message);
        film.setLike(userId);
    }

    public void deleteUsersLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);
        String message = "Удалил лайк у фильма " + filmId + " от пользователя " + userId;
        log.info(message);
        film.deleteLike(userId);
    }

    public List<Film> getTopFilmsByLikes(int countFilms) {
        if (countFilms == 0) {
            throw new ValidationException("Количество фильмов не может быть отрицательным!");
        }
        List<Film> filmList = getFilmsList();
        filmList.sort((film1, film2) -> {
            if (film1.getCountLikes() == film2.getCountLikes()) {
                return film2.getId() - film1.getId();
            } else {
                return film2.getCountLikes() - film1.getCountLikes();
            }
        });
        return filmList.stream()
                .filter(x -> x.getCountLikes() != 0)
                .limit(countFilms)
                .collect(Collectors.toList());
    }

    private int getNextId() {
        return getFilmsList().size() + 1;
    }
}