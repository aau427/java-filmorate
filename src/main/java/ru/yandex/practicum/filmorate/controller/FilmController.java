package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.FilmManager;
import ru.yandex.practicum.filmorate.manager.Managers;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@RequestMapping("/films")
@RestController
public class FilmController {
    private final FilmManager manager = Managers.getDefaultFilmManager();

    @GetMapping
    public List<Film> getFilmList() {
        return manager.getFilmsList();
    }

    @PostMapping()
    public Film createFilm(@Valid @RequestBody Film film) {
        return manager.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return manager.updateFilm(film);
    }

    @DeleteMapping
    public void deleteAllFilms() {
        manager.deleteAllFilms();
    }
}
