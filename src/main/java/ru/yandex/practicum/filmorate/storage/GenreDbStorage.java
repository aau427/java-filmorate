package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private final String sqlForGetItem = "SELECT * FROM GENRES WHERE ID = ?";
    private final String sqlForGetAllItem = "SELECT * FROM GENRES";

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, (resultSet, rowNum) ->
                new Genre(resultSet.getInt("id"), resultSet.getString("name")));
    }

    @Override
    public Optional<Genre> getGenreById(int idGenre) {
        return getItemById(sqlForGetItem, idGenre);
    }

    @Override
    public List<Genre> getAllGenres() {
        return getAllItems(sqlForGetAllItem);
    }
}
