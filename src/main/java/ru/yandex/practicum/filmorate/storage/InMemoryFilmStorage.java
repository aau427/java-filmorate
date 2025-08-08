package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("INMEMORYFILMSTORAGE")
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

    @Override
    public void setUsersLike(Film film, int userId) {
        film.setLike(userId);
    }

    @Override
    public void deleteUsersLike(Film film, int userId) {
        film.deleteLike(userId);
    }

    @Override
    public List<Film> getTopFilmsByLikes(int countFilms) {
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
}
