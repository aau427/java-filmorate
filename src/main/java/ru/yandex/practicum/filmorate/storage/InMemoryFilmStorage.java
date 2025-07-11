package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmsMap = new HashMap<>();

    @Override
    public Film createFilm(final Film film) {
        filmsMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(final Film film) {
        filmsMap.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmsMap.values());
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(filmsMap.get(id));
    }

    @Override
    public void deleteAllFilms() {
        filmsMap.clear();
    }
}
