package ru.yandex.practicum.filmorate.comparator;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmComparatorByLikes implements Comparator<Film> {
    @Override
    public int compare(Film film, Film anotherFilm) {
        if (film.getCountLikes() == anotherFilm.getCountLikes()) {
            return film.getId() - anotherFilm.getId();
        } else {
            return anotherFilm.getCountLikes() - film.getCountLikes();
        }
    }
}
