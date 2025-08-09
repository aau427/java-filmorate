package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage storage;

    public List<Genre> getAllGenres() {
        List<Genre> genreList = storage.getAllGenres();
        log.info("Service: Отгрузил жанры в количестве {}", genreList.size());
        return genreList;
    }

    public Genre getGenreById(int genreId) {
        Optional<Genre> optGenre = storage.getGenreById(genreId);
        if (!optGenre.isPresent()) {
            String msg = "Не нашел жанр с Id = " + genreId;
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return optGenre.get();
    }
}
