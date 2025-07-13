package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RequestMapping("/films")
@RestController
@RequiredArgsConstructor
public class FilmController {
    private static final String DEFAULT_FILM_COUNT = "10";
    private final FilmService service;

    @GetMapping
    public List<Film> getFilmList() {
        return service.getFilmsList();
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        return service.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return service.updateFilm(film);
    }

    @DeleteMapping
    public void deleteAllFilms() {
        service.deleteAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void setUsersLike(@PathVariable("id") int filmId,
                             @PathVariable int userId) {
        service.setUsersLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteUsersLike(@PathVariable("id") int filmId,
                                @PathVariable int userId) {
        service.deleteUsersLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilmsByLikes(@RequestParam(defaultValue = DEFAULT_FILM_COUNT) int count) {
        return service.getTopFilmsByLikes(count);
    }
}