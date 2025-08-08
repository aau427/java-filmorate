package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Component
public class RatingDbStorage extends BaseRepository<Rating> implements RatingStorage {

    String sqlForItem = "SELECT R.ID ID, R.NAME NAME FROM RATINGS R WHERE ID = ?";
    String sqlForAllItems = "SELECT R.ID, R.NAME FROM RATINGS R";

    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, (resultSet, rowNum) -> {
            return new Rating(resultSet.getInt("ID"), resultSet.getString("NAME"));
        });
    }

    @Override
    public Optional<Rating> getRatingById(int idRating) {
        return getItemById(sqlForItem, idRating);
    }

    @Override
    public List<Rating> getAllRatings() {
        return getAllItems(sqlForAllItems);
    }
}
