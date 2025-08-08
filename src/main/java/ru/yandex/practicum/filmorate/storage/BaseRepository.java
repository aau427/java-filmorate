package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> mapper;

    protected Optional<T> getItemById(String sql, int id) {
        try {
            Optional<T> t = Optional.ofNullable(jdbcTemplate.queryForObject(sql, mapper, id));
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, mapper, id));
        } catch (DataAccessException exception) {
            return Optional.empty();
        }

    }

    protected List<T> getAllItems(String sql) {
        return jdbcTemplate.query(sql, mapper);
    }
}
