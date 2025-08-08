package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class FilmControllerTest {
    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() throws Exception {
        mockMvc.perform(
                delete("/films"));
        mockMvc.perform(
                delete("/users"));
    }

    @Test
    @DisplayName("Корректный фильм создается")
    public void shouldCorrectFimCreate() throws Exception {
        Film film = Film.builder()
                .name("фильм")
                .description("корректный фильм")
                .duration(10)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .mpa(new Rating(1, "какой-то"))
                .build();

        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("фильм"))
                .andExpect(jsonPath("$.description").value("корректный фильм"));
    }

    @Test
    @DisplayName("Фильм не создается без имени")
    public void shouldFilmNotCreateWithoutName() throws Exception {
        Film film = Film.builder()
                .description("я вешаюсь")
                .duration(10)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(400));
    }

    @Test
    @DisplayName("Фильм не создается с отрицательной длительностью")
    public void shouldFilmNotCreateWithNegativeDuration() throws Exception {
        Film film = Film.builder()
                .name("Это не ты смотришь, это за тобой смотрят")
                .description("какой-то фильм")
                .duration(-5)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("Фильм не создается с 0 длительностью")
    public void shouldFilmNotCreateWithZeroDuration() throws Exception {
        Film film = Film.builder()
                .name("Это не ты смотришь, это за тобой смотрят")
                .description("заклей камеру")
                .duration(0)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("Фильм не создается без описания")
    public void shouldFilmNotCreateWithoutDescription() throws Exception {
        Film film = Film.builder()
                .name("Борзые перцы")
                .duration(-5)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("Фильм не создается с описание длиной 201")
    public void shouldFilmNotCreateWith201Description() throws Exception {
        StringBuilder longDescription = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            longDescription = longDescription.append("a");
        }
        Film film = Film.builder()
                .name("просто фильм")
                .description(longDescription.toString())
                .duration(20)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Фильм создается с описание длиной 200")
    public void shouldFilmCreateWith201Description() throws Exception {
        StringBuilder longDescription = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            longDescription = longDescription.append("a");
        }
        Film film = Film.builder()
                .name("просто фильм")
                .description(longDescription.toString())
                .duration(20)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .mpa(new Rating(1, "какой-то"))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200));
    }

    @Test
    @DisplayName("Фильм не создается с датой 27.12.1895")
    public void shouldFilmNotCreateWithWrongReleaseDate() throws Exception {
        Film film = Film.builder()
                .name("просто фильм")
                .description("описание фильма")
                .duration(20)
                .releaseDate(minReleaseDate.minusDays(1))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(x -> Objects.requireNonNull(x.getResolvedException()).getClass().equals(ValidationException.class));
    }

    @Test
    @DisplayName("Фильм создается с датой 28.12.1895")
    public void shouldFilmCreateWithMinReleaseDate() throws Exception {
        Film film = Film.builder()
                .name("просто фильм")
                .description("описание фильма")
                .duration(20)
                .releaseDate(minReleaseDate)
                .mpa(new Rating(1, "какой-то"))
                .build();
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200));
    }

    @Test
    @DisplayName("Фильм изменяется")
    public void shouldFilmUpdate() throws Exception {
        Film film = Film.builder()
                .name("просто фильм")
                .description("описание фильма")
                .duration(20)
                .releaseDate(minReleaseDate)
                .mpa(new Rating(1, "какой-то"))
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        film = Film.builder()
                .id(1)
                .name("изменил название")
                .description("изменил описание")
                .duration(22)
                .releaseDate(LocalDate.of(2025, 7, 7))
                .mpa(new Rating(1, "какой-то"))
                .build();

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("изменил название"))
                .andExpect(jsonPath("$.description").value("изменил описание"))
                .andExpect(jsonPath("$.duration").value(22))
                .andExpect(jsonPath("$.releaseDate").value("2025-07-07"));

    }

    @Test
    @DisplayName("Несуществующий фильм не изменяется")
    public void shouldFilmNotCreateWithoutId() throws Exception {
        Film film = Film.builder()
                .name("просто фильм")
                .description("описание фильма")
                .duration(20)
                .releaseDate(minReleaseDate)
                .build();
        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        film = Film.builder()
                .name("изменил название")
                .description("изменил описание")
                .duration(22)
                .releaseDate(LocalDate.of(2025, 7, 7))
                .mpa(new Rating(1, "какой-то"))
                .build();

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Пользователь может поставить лайк фильму")
    public void shouldUserSetLikeToFilm() throws Exception {
        Film film = Film.builder()
                .name("фильм")
                .description("его описание")
                .duration(10)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .mpa(new Rating(1, "какой-то"))
                .build();

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        User user = User.builder()
                .name("Пользователь 1")
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().is(200));
    }

    @Test
    @DisplayName("Пользователь не может поставить лайк несуществующему фильму")
    public void shouldNotUserSetLikeToNotFoundFilm() throws Exception {
        User user = User.builder()
                .name("Пользователь 1")
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                        put("/films/1/like/1"))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Нельзя поставить фильму лайк от несуществующего пользователя")
    public void shouldNotSetLikeToFilmFromNotFoundUser() throws Exception {
        Film film = Film.builder()
                .name("фильм")
                .description("его описание")
                .duration(10)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                        put("/films/1/like/1"))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Пользователь может удалить лайк")
    public void shouldUserDeleteLike() throws Exception {
        Film film = Film.builder()
                .name("фильм")
                .description("его описание")
                .duration(10)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .mpa(new Rating(1, "какой-то"))
                .build();

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        User user = User.builder()
                .name("Пользователь 1")
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                put("/films/1/like/1"));

        mockMvc.perform(
                        delete("/films/1/like/1"))
                .andExpect(status().is(200));
    }

    @Test
    @DisplayName("Пользователь не может удалить лайк у несуществующего фильма")
    public void shouldNotUserDeleteLikeFromNotFoundFilm() throws Exception {
        Film film = Film.builder()
                .name("фильм")
                .description("его описание")
                .duration(10)
                .releaseDate(LocalDate.of(1970, 10, 5))
                .build();

        mockMvc.perform(
                post("/films")
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        User user = User.builder()
                .name("Пользователь 1")
                .email("aaa@mail.ru")
                .birthday(LocalDate.now().minusYears(25))
                .login("aaymail")
                .build();

        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(
                put("/films/1/like/1"));

        mockMvc.perform(
                delete("/films"));


        mockMvc.perform(
                        delete("/films/1/like/1"))
                .andExpect(status().is(404));
    }
}