package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class InMemoryFilmManager implements FilmManager {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> filmsMap = new HashMap<>();
    private static int filmId = 0;

    @Override
    public Film createFilm(final Film film) {
        validateFilm(film);
        film.setId(getNextId());
        filmsMap.put(film.getId(), film);
        log.info("Создан новый фильм {}", film.getId());
        return film.clone();
    }

    @Override
    public Film updateFilm(final Film film) {
        validateFilm(film);
        if (!filmsMap.containsKey(film.getId())) {
            String message = "Ошибка при изменении фильма: не найден филь с Id = " + film.getId();
            log.error(message);
            throw new ResourceNotFoundException(message);
        }
        filmsMap.put(film.getId(), film);
        log.info("Изменил фильм: {}", film.getId());
        return film.clone();
    }

    @Override
    public List<Film> getFilmsList() {
        log.info("Отгрузил фильмы в количестве {}", filmsMap.size());
        return new ArrayList<>(filmsMap.values());
    }

    @Override
    public Film getFilmById(int id) {
        if (!filmsMap.containsKey(id)) {
            String message = "Ошибка: не найден филь с Id = " + id;
            log.error(message);
            throw new ResourceNotFoundException(message);
        }
        return filmsMap.get(id).clone();
    }

    private int getNextId() {
        return ++filmId;
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String msg = "Ошибка! Дата релиза не может быть ранее дня рождения кино";
            log.warn(msg);
            throw new ValidationException(msg);
        }
    }
}
