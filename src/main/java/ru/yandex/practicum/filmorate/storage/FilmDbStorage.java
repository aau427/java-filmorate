package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("FILMDBSSTORAGE")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource()).withTableName("FILMS")
                .usingGeneratedKeyColumns("ID");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("NAME", film.getName());
        parameters.put("DESCRIPTION", film.getDescription());
        parameters.put("RELEASEDATE", film.getReleaseDate());
        parameters.put("DURATION", film.getDuration());
        parameters.put("RATING", film.getMpa().getId());
        int filmId = (int) jdbcInsert.executeAndReturnKey(parameters);
        film.setId(filmId);
        insertAllGenresToFilm(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILMS SET "
                + "NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, RATING = ? WHERE ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        deleteAllGenresFromFilm(film);
        insertAllGenresToFilm(film);
        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        String sql = "SELECT F.ID FILM_ID, "
                + "F.NAME FILM_NAME, "
                + "F.DESCRIPTION FILM_DESCRIPTION, "
                + "F.RELEASEDATE FILM_DATE, "
                + "F.DURATION FILM_DURATION, "
                + "FL.USER_ID LIKE_USER_ID, "
                + "G.ID   GENRE_ID, "
                + "G.NAME GENRE_NAME, "
                + "R.ID MPA_ID, "
                + "R.NAME MPA_NAME "
                + "FROM FILMS F "
                + "LEFT JOIN FILMLIKES Fl on FL.FILM_ID = F.ID "
                + "LEFT JOIN FILMSGENRES FG on F.ID = FG.FILM_ID  "
                + "lEFT JOIN GENRES G ON FG.GENRE_ID = G.ID "
                + "LEFT JOIN RATINGS R on F.RATING = R.ID";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        return getFilmListFromRowSet(rowSet);
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        String sql = "SELECT F.ID FILM_ID, "
                + "F.NAME FILM_NAME, "
                + "F.DESCRIPTION FILM_DESCRIPTION, "
                + "F.RELEASEDATE FILM_DATE, "
                + "F.DURATION FILM_DURATION, "
                + "FL.USER_ID LIKE_USER_ID, "
                + "G.ID   GENRE_ID, "
                + "G.NAME GENRE_NAME, "
                + "R.ID MPA_ID, "
                + "R.NAME MPA_NAME "
                + "FROM FILMS F "
                + "LEFT JOIN FILMLIKES Fl on FL.FILM_ID = F.ID "
                + "LEFT JOIN FILMSGENRES FG on F.ID = FG.FILM_ID "
                + "lEFT JOIN GENRES G  ON FG.GENRE_ID = G.ID "
                + "LEFT JOIN RATINGS R on F.RATING = R.ID "
                + "WHERE F.ID = ? ";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, filmId);
        List<Film> filmList = getFilmListFromRowSet(rowSet);
        if (!filmList.isEmpty()) {
            return Optional.of(filmList.getFirst());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteAllFilms() {
        jdbcTemplate.update("DELETE FROM FILMLIKES");
        jdbcTemplate.update("DELETE FROM FILMSGENRES");
        jdbcTemplate.update("DELETE FROM FILMS");
    }

    @Override
    public void setUsersLike(Film film, int userId) {
        String sql = "INSERT INTO FILMLIKES(FILM_ID, USER_ID) VALUES(?,?)";
        jdbcTemplate.update(sql, film.getId(), userId);
    }

    @Override
    public void deleteUsersLike(Film film, int userId) {
        String sql = "DELETE FROM FILMLIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, film.getId(), userId);
    }

    /*Возвращает список из первых countFilms фильмов по количеству лайков.
     */
    @Override
    public List<Film> getTopFilmsByLikes(int countFilms) {
        String sql = "SELECT F1.ID FILM_ID, "
                + "F1.NAME FILM_NAME, "
                + "F1.DESCRIPTION FILM_DESCRIPTION, "
                + "F1.RELEASEDATE FILM_DATE, "
                + "F1.DURATION FILM_DURATION, "
                + "G.ID GENRE_ID, "
                + "G.NAME GENRE_NAME, "
                + "FL.USER_ID LIKE_USER_ID, "
                + "R.ID MPA_ID, "
                + "R.NAME MPA_NAME "
                + "FROM (SELECT F.ID, "
                + "COUNT(fl.*) count_likes "
                + "FROM FILMS F "
                + "LEFT JOIN FILMLIKES FL ON F.ID = FL.FILM_ID "
                + "GROUP BY F.ID "
                + "ORDER BY COUNT(FL.*) DESC "
                + "LIMIT 10) TOPFILMS "
                + "JOIN FILMS F1 ON F1.ID = TOPFILMS.ID "
                + "LEFT JOIN FILMSGENRES FG ON FG.FILM_ID = F1.ID "
                + "LEFT JOIN GENRES G ON G.ID = FG.GENRE_ID "
                + "LEFT JOIN FILMLIKES Fl on FL.FILM_ID = F1.ID "
                + "LEFT JOIN RATINGS R ON R.ID = F1.RATING ";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        return getFilmListFromRowSet(rowSet);
    }

    private void insertAllGenresToFilm(Film film) {
        jdbcTemplate.batchUpdate("INSERT INTO FILMSGENRES(FILM_ID, GENRE_ID) VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    private List<Genre> tmpList = new ArrayList<>(film.getGenres());

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, tmpList.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
    }

    private void deleteAllGenresFromFilm(Film film) {
        String sql = "DELETE FROM FILMSGENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private List<Film> getFilmListFromRowSet(SqlRowSet rowSet) {
        Map<Integer, Film> filmsMap = new LinkedHashMap<>();
        while (rowSet.next()) {
            int filmId;
            Film currentFilm;
            filmId = rowSet.getInt("FILM_ID");
            if (!filmsMap.containsKey(filmId)) {
                currentFilm = createFilm(rowSet);
            } else {
                currentFilm = filmsMap.get(filmId);
            }
            if (rowSet.getInt("GENRE_ID") != 0) {
                Genre genre = makeGenreFromRs(rowSet);
                if (!currentFilm.getGenres().contains(genre)) {
                    currentFilm.setGenre(genre);
                }
            }
            if (rowSet.getInt("LIKE_USER_ID") != 0) {
                currentFilm.setLike(rowSet.getInt("LIKE_USER_ID"));
            }
            filmsMap.put(filmId, currentFilm);
        }
        return new ArrayList<>(filmsMap.values());
    }

    private Film createFilm(SqlRowSet rowSet) {
        return Film.builder()
                .id(rowSet.getInt("FILM_ID"))
                .name(rowSet.getString("FILM_NAME"))
                .description(rowSet.getString("FILM_DESCRIPTION"))
                .releaseDate(rowSet.getDate("FILM_DATE").toLocalDate())
                .duration(rowSet.getInt("FILM_DURATION"))
                .mpa(new Rating(rowSet.getInt("MPA_ID"), rowSet.getString("MPA_NAME")))
                .build();
    }

    private Genre makeGenreFromRs(SqlRowSet rowSet) {
        return new Genre(rowSet.getInt("GENRE_ID"), rowSet.getString("GENRE_NAME"));
    }
}
